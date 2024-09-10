/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain

import android.util.Log
import kotlinx.coroutines.runBlocking
import org.bitcoindevkit.Address
import org.bitcoindevkit.AddressInfo
import org.rustbitcoin.bitcoin.Amount
import org.bitcoindevkit.CanonicalTx
import org.bitcoindevkit.ChainPosition
import org.bitcoindevkit.Connection
import org.bitcoindevkit.Descriptor
import org.bitcoindevkit.DescriptorSecretKey
import org.rustbitcoin.bitcoin.FeeRate
import org.bitcoindevkit.KeychainKind
import org.bitcoindevkit.Mnemonic
import org.rustbitcoin.bitcoin.Network
import org.bitcoindevkit.Psbt
import org.rustbitcoin.bitcoin.Script
import org.bitcoindevkit.TxBuilder
import org.bitcoindevkit.Update
import org.bitcoindevkit.WordCount
import org.bitcoindevkit.devkitwallet.data.ActiveWalletScriptType
import org.bitcoindevkit.devkitwallet.data.ConfirmationBlock
import org.bitcoindevkit.devkitwallet.data.NewWalletConfig
import org.bitcoindevkit.devkitwallet.data.RecoverWalletConfig
import org.bitcoindevkit.devkitwallet.data.SingleWallet
import org.bitcoindevkit.devkitwallet.data.Timestamp
import org.bitcoindevkit.devkitwallet.data.TxDetails
import org.bitcoindevkit.devkitwallet.domain.utils.intoDomain
import org.bitcoindevkit.devkitwallet.domain.utils.intoProto
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.Recipient
import org.bitcoindevkit.Wallet as BdkWallet
import java.util.UUID

private const val TAG = "Wallet"

class Wallet private constructor(
    private val wallet: BdkWallet,
    private val recoveryPhrase: String,
    private val connection: Connection,
    private var fullScanCompleted: Boolean,
    private val walletId: String,
    private val userPreferencesRepository: UserPreferencesRepository,
    blockchainClientsConfig: BlockchainClientsConfig
) {
    private var currentBlockchainClient: BlockchainClient? = blockchainClientsConfig.getClient()

    fun getRecoveryPhrase(): List<String> {
        return recoveryPhrase.split(" ")
    }

    fun createTransaction(
        recipientList: List<Recipient>,
        feeRate: FeeRate,
        disableRbf: Boolean,
        opReturnMsg: String?
    ): Psbt {
        // technique 1 for adding a list of recipients to the TxBuilder
        // var txBuilder = TxBuilder()
        // for (recipient in recipientList) {
        //     txBuilder  = txBuilder.addRecipient(address = recipient.first, amount = recipient.second)
        // }
        // txBuilder = txBuilder.feeRate(satPerVbyte = fee_rate)

        // technique 2 for adding a list of recipients to the TxBuilder
        var txBuilder = recipientList.fold(TxBuilder()) { builder, recipient ->
            // val address = Address(recipient.address)
            val scriptPubKey: Script = Address(recipient.address, Network.TESTNET).scriptPubkey()
            builder.addRecipient(scriptPubKey, Amount.fromSat(recipient.amount))
        }
        if (disableRbf) {
            // Nothing
        } else {
            txBuilder = txBuilder.enableRbf()
        }
        // if (!opReturnMsg.isNullOrEmpty()) {
        //     txBuilder = txBuilder.addData(opReturnMsg.toByteArray(charset = Charsets.UTF_8).asUByteArray().toList())
        // }
        return txBuilder.feeRate(feeRate).finish(wallet)
    }

    // @OptIn(ExperimentalUnsignedTypes::class)
    // fun createSendAllTransaction(
    //     recipient: String,
    //     feeRate: Float,
    //     enableRBF: Boolean,
    //     opReturnMsg: String?
    // ): PartiallySignedTransaction {
    //     val scriptPubkey: Script = Address(recipient).scriptPubkey()
    //     var txBuilder = TxBuilder()
    //         .drainWallet()
    //         .drainTo(scriptPubkey)
    //         .feeRate(satPerVbyte = feeRate)
    //
    //     if (enableRBF) {
    //         txBuilder = txBuilder.enableRbf()
    //     }
    //     if (!opReturnMsg.isNullOrEmpty()) {
    //         txBuilder = txBuilder.addData(opReturnMsg.toByteArray(charset = Charsets.UTF_8).asUByteArray().toList())
    //     }
    //     return txBuilder.finish(wallet).psbt
    // }

    // fun createBumpFeeTransaction(txid: String, feeRate: Float): PartiallySignedTransaction {
    //     return BumpFeeTxBuilder(txid = txid, newFeeRate = feeRate)
    //         .enableRbf()
    //         .finish(wallet = wallet)
    // }

    fun sign(psbt: Psbt): Boolean {
        return wallet.sign(psbt)
    }

    fun broadcast(signedPsbt: Psbt): String {
        currentBlockchainClient?.broadcast(signedPsbt.extractTx()) ?: throw IllegalStateException("Blockchain client not initialized")
        return signedPsbt.extractTx().computeTxid()
    }

    private fun getAllTransactions(): List<CanonicalTx>  = wallet.transactions()

    fun getAllTxDetails(): List<TxDetails> {
        val transactions = getAllTransactions()
        return transactions.map { tx ->
            val txid = tx.transaction.computeTxid()
            val (sent, received) = wallet.sentAndReceived(tx.transaction)
            var feeRate: FeeRate? = null
            var fee: Amount? = null
            // TODO: I don't know why we're getting negative fees here, but it looks like a bug
            try {
                fee = wallet.calculateFee(tx.transaction)
            } catch (e: Exception) {
                Log.e(TAG, "Error calculating fee rate for tx $txid: $e")
            }
            try {
                feeRate = wallet.calculateFeeRate(tx.transaction)
            } catch (e: Exception) {
                Log.e(TAG, "Error calculating fee for tx $txid: $e")
            }

            val (confirmationBlock, confirmationTimestamp, pending) = when (val position = tx.chainPosition) {
                is ChainPosition.Unconfirmed -> Triple(null, null, true)
                is ChainPosition.Confirmed -> Triple(ConfirmationBlock(position.confirmationBlockTime.blockId.height), Timestamp(position.confirmationBlockTime.confirmationTime), false)
            }
            TxDetails(tx.transaction, txid, sent.toSat(), received.toSat(), fee?.toSat() ?: 0uL, feeRate, pending, confirmationBlock, confirmationTimestamp)
        }
    }

    // fun getTransaction(txid: String): TransactionDetails? {
    //     val allTransactions = getAllTransactions()
    //     allTransactions.forEach {
    //         if (it.txid == txid) {
    //             return it
    //         }
    //     }
    //     return null
    // }

    // fun sync() {
    //     val fullScanRequest = wallet.startFullScan().build()
    //     val update: Update = currentBlockchainClient?.fullScan(fullScanRequest, 20u) ?: throw IllegalStateException("Blockchain client not initialized")
    //     Log.i(TAG, "Wallet sync complete with update $update")
    //     wallet.applyUpdate(update)
    //     wallet.persist(connection)
    // }

    private fun fullScan() {
        val fullScanRequest = wallet.startFullScan().build()
        val update: Update = currentBlockchainClient?.fullScan(
            fullScanRequest = fullScanRequest,
            stopGap = 20u,
        ) ?: throw IllegalStateException("Blockchain client not initialized")
        wallet.applyUpdate(update)
        wallet.persist(connection)
    }

    fun sync() {
        if (!fullScanCompleted) {
            Log.i(TAG, "Full scan required")
            fullScan()
            runBlocking {
                userPreferencesRepository.setFullScanCompleted(walletId)
                fullScanCompleted = true
            }
        } else {
            Log.i(TAG, "Just a normal sync!")
            val syncRequest = wallet.startSyncWithRevealedSpks().build()
            val update = currentBlockchainClient?.sync(
                syncRequest = syncRequest,
            ) ?: throw IllegalStateException("Blockchain client not initialized")
            wallet.applyUpdate(update)
            wallet.persist(connection)
        }
    }

    fun getBalance(): ULong = wallet.balance().total.toSat()

    fun getNewAddress(): AddressInfo = wallet.revealNextAddress(KeychainKind.EXTERNAL)

    // fun getLastUnusedAddress(): AddressInfo = wallet.getAddress(AddressIndex.LastUnused)

    // fun isBlockChainCreated() = ::electrumServer.isInitialized

    // fun getElectrumURL(): String = electrumServer.getElectrumURL()

    // fun isElectrumServerDefault(): Boolean = electrumServer.isElectrumServerDefault()

    // fun setElectrumSettings(electrumSettings: ElectrumSettings) {
    //     when (electrumSettings) {
    //         ElectrumSettings.DEFAULT -> electrumServer.useDefaultElectrum()
    //         ElectrumSettings.CUSTOM ->  electrumServer.useCustomElectrum()
    //     }
    // }

    companion object {
        fun createWallet(
            newWalletConfig: NewWalletConfig,
            internalAppFilesPath: String,
            userPreferencesRepository: UserPreferencesRepository,
        ): Wallet {
            val mnemonic = Mnemonic(WordCount.WORDS12)
            val bip32ExtendedRootKey = DescriptorSecretKey(newWalletConfig.network, mnemonic, null)
            val descriptor: Descriptor = createScriptAppropriateDescriptor(
                newWalletConfig.scriptType,
                bip32ExtendedRootKey,
                newWalletConfig.network,
                KeychainKind.EXTERNAL
            )
            val changeDescriptor: Descriptor = createScriptAppropriateDescriptor(
                newWalletConfig.scriptType,
                bip32ExtendedRootKey,
                newWalletConfig.network,
                KeychainKind.INTERNAL
            )
            val walletId = UUID.randomUUID().toString()
            val connection = Connection("$internalAppFilesPath/wallet-${walletId.take(8)}.sqlite3",)

            // Create SingleWallet object for saving to datastore
            val newWalletForDatastore: SingleWallet = SingleWallet.newBuilder()
                .setId(walletId)
                .setName(newWalletConfig.name)
                .setNetwork(newWalletConfig.network.intoProto())
                .setScriptType(ActiveWalletScriptType.P2WPKH)
                .setDescriptor(descriptor.toStringWithSecret())
                .setChangeDescriptor(changeDescriptor.toStringWithSecret())
                .setRecoveryPhrase(mnemonic.toString())
                .build()

            // TODO: launch this correctly, not on the main thread
            // Save the new wallet to the datastore
            runBlocking { userPreferencesRepository.updateActiveWallets(newWalletForDatastore) }

            val bdkWallet = BdkWallet(
                descriptor = descriptor,
                changeDescriptor = changeDescriptor,
                network = newWalletConfig.network,
                connection = connection,
            )

            return Wallet(
                wallet = bdkWallet,
                recoveryPhrase = mnemonic.toString(),
                connection = connection,
                fullScanCompleted = false,
                walletId = walletId,
                userPreferencesRepository = userPreferencesRepository,
                blockchainClientsConfig = BlockchainClientsConfig.createDefaultConfig(newWalletConfig.network)
            )
        }

        fun loadActiveWallet(
            activeWallet: SingleWallet,
            internalAppFilesPath: String,
            userPreferencesRepository: UserPreferencesRepository,
        ): Wallet {
            val descriptor = Descriptor(activeWallet.descriptor, activeWallet.network.intoDomain())
            val changeDescriptor = Descriptor(activeWallet.changeDescriptor, activeWallet.network.intoDomain())
            val connection = Connection("$internalAppFilesPath/wallet-${activeWallet.id.take(8)}.sqlite3")
            val bdkWallet = BdkWallet.load(
                descriptor = descriptor,
                changeDescriptor = changeDescriptor,
                connection = connection,
            )

            return Wallet(
                wallet = bdkWallet,
                recoveryPhrase = activeWallet.recoveryPhrase,
                connection = connection,
                fullScanCompleted = activeWallet.fullScanCompleted,
                walletId = activeWallet.id,
                userPreferencesRepository = userPreferencesRepository,
                blockchainClientsConfig = BlockchainClientsConfig.createDefaultConfig(activeWallet.network.intoDomain())
            )
        }

        fun recoverWallet(
            recoverWalletConfig: RecoverWalletConfig,
            internalAppFilesPath: String,
            userPreferencesRepository: UserPreferencesRepository,
        ): Wallet {
            val mnemonic = Mnemonic.fromString(recoverWalletConfig.recoveryPhrase)
            val bip32ExtendedRootKey = DescriptorSecretKey(recoverWalletConfig.network, mnemonic, null)
            val descriptor: Descriptor = createScriptAppropriateDescriptor(
                recoverWalletConfig.scriptType,
                bip32ExtendedRootKey,
                recoverWalletConfig.network,
                KeychainKind.EXTERNAL
            )
            val changeDescriptor: Descriptor = createScriptAppropriateDescriptor(
                recoverWalletConfig.scriptType,
                bip32ExtendedRootKey,
                recoverWalletConfig.network,
                KeychainKind.INTERNAL
            )
            val walletId = UUID.randomUUID().toString()
            val connection = Connection("$internalAppFilesPath/wallet-${walletId.take(8)}.sqlite3",)

            // Create SingleWallet object for saving to datastore
            val newWalletForDatastore: SingleWallet = SingleWallet.newBuilder()
                .setId(walletId)
                .setName(recoverWalletConfig.name)
                .setNetwork(recoverWalletConfig.network.intoProto())
                .setScriptType(ActiveWalletScriptType.P2WPKH)
                .setDescriptor(descriptor.toStringWithSecret())
                .setChangeDescriptor(changeDescriptor.toStringWithSecret())
                .setRecoveryPhrase(mnemonic.toString())
                .build()

            // TODO: launch this correctly, not on the main thread
            // Save the new wallet to the datastore
            runBlocking { userPreferencesRepository.updateActiveWallets(newWalletForDatastore) }

            val bdkWallet = BdkWallet(
                descriptor = descriptor,
                changeDescriptor = changeDescriptor,
                connection = connection,
                network = recoverWalletConfig.network,
            )

            return Wallet(
                wallet = bdkWallet,
                recoveryPhrase = mnemonic.toString(),
                connection = connection,
                fullScanCompleted = false,
                walletId = walletId,
                userPreferencesRepository = userPreferencesRepository,
                blockchainClientsConfig = BlockchainClientsConfig.createDefaultConfig(recoverWalletConfig.network)
            )
        }
    }
}

fun createScriptAppropriateDescriptor(
    scriptType: ActiveWalletScriptType,
    bip32ExtendedRootKey: DescriptorSecretKey,
    network: Network,
    keychain: KeychainKind
): Descriptor {
    return if (keychain == KeychainKind.EXTERNAL) {
        when (scriptType) {
            ActiveWalletScriptType.P2WPKH -> Descriptor.newBip84(bip32ExtendedRootKey, KeychainKind.EXTERNAL, network)
            ActiveWalletScriptType.P2TR -> Descriptor.newBip86(bip32ExtendedRootKey, KeychainKind.EXTERNAL, network)
            ActiveWalletScriptType.UNRECOGNIZED -> TODO()
        }
    } else {
        when (scriptType) {
            ActiveWalletScriptType.P2WPKH -> Descriptor.newBip84(bip32ExtendedRootKey, KeychainKind.INTERNAL, network)
            ActiveWalletScriptType.P2TR -> Descriptor.newBip86(bip32ExtendedRootKey, KeychainKind.INTERNAL, network)
            ActiveWalletScriptType.UNRECOGNIZED -> TODO()
        }
    }
}
