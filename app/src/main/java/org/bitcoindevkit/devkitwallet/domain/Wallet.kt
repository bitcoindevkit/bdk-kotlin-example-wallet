/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain

import android.util.Log
import kotlinx.coroutines.runBlocking
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.Recipient
import org.bitcoindevkit.Network
import org.bitcoindevkit.Address
import org.bitcoindevkit.Descriptor
import org.bitcoindevkit.DescriptorSecretKey
import org.bitcoindevkit.KeychainKind
import org.bitcoindevkit.Mnemonic
import org.bitcoindevkit.WordCount
import org.bitcoindevkit.TxBuilder
import org.bitcoindevkit.PartiallySignedTransaction
import org.bitcoindevkit.AddressInfo
import org.bitcoindevkit.AddressIndex
import org.bitcoindevkit.CanonicalTx
import org.bitcoindevkit.ChainPosition
import org.bitcoindevkit.FeeRate
import org.bitcoindevkit.Update
import org.bitcoindevkit.Script
import org.bitcoindevkit.devkitwallet.data.RecoverWalletConfig
import org.bitcoindevkit.devkitwallet.data.ActiveWalletNetwork
import org.bitcoindevkit.devkitwallet.data.ActiveWalletScriptType
import org.bitcoindevkit.devkitwallet.data.ConfirmationBlock
import org.bitcoindevkit.devkitwallet.data.SingleWallet
import org.bitcoindevkit.devkitwallet.data.Timestamp
import org.bitcoindevkit.devkitwallet.data.TxDetails
import org.bitcoindevkit.devkitwallet.domain.utils.intoProto
import org.bitcoindevkit.Wallet as BdkWallet

private const val TAG = "Wallet"

object Wallet {
    private lateinit var wallet: BdkWallet
    private lateinit var path: String
    private lateinit var recoveryPhrase: String
    private var currentBlockchainClient: BlockchainClient? = null
    private val blockchainClients: MutableMap<ClientRank, BlockchainClient> = mutableMapOf()

    init {
        blockchainClients.put(ClientRank.DEFAULT, EsploraClient("https://esplora.testnet.kuutamo.cloud/"))
        // blockchainClients.put(ClientRank.DEFAULT, EsploraClient("https://blockstream.info/testnet/api/"))
        currentBlockchainClient = blockchainClients[ClientRank.DEFAULT]
    }
    // private lateinit var electrumServer: ElectrumServer
    // private val esploraClient: EsploraClient = EsploraClient("http://10.0.2.2:3002")
    // private val esploraClient: EsploraClient = EsploraClient("https://esplora.testnet.kuutamo.cloud/")
    // to use Esplora on regtest locally, use the following address
    // private const val regtestEsploraUrl: String = "http://10.0.2.2:3002"

    // setting the path requires the application context and is done once by the DevkitWalletApplication class
    fun setPath(path: String) {
        Wallet.path = path
    }

    private fun initialize(
        descriptor: Descriptor,
        changeDescriptor: Descriptor?,
    ) {
        val databasePath = "$path/wallet.db"
        wallet = BdkWallet(
            descriptor,
            changeDescriptor,
            databasePath,
            Network.TESTNET,
        )
    }

    // fun createBlockchain() {
        // electrumServer = ElectrumServer()
        // Log.i(TAG, "Current electrum URL : ${electrumServer.getElectrumURL()}")
    // }

    // fun changeElectrumServer(electrumURL: String) {
    //     electrumServer.createCustomElectrum(electrumURL = electrumURL)
    //     wallet.sync(electrumServer.server, LogProgress)
    // }

    fun createWallet(activeWalletsRepository: ActiveWalletsRepository) {
        val mnemonic = Mnemonic(WordCount.WORDS12)
        val bip32ExtendedRootKey = DescriptorSecretKey(Network.TESTNET, mnemonic, null)
        val descriptor: Descriptor = Descriptor.newBip84(bip32ExtendedRootKey, KeychainKind.EXTERNAL, Network.TESTNET)
        val changeDescriptor: Descriptor = Descriptor.newBip84(bip32ExtendedRootKey, KeychainKind.INTERNAL, Network.TESTNET)
        initialize(
            descriptor = descriptor,
            changeDescriptor = changeDescriptor,
        )
        recoveryPhrase = mnemonic.asString()
        val newWallet: SingleWallet = SingleWallet.newBuilder()
            .setName("Wallet")
            .setNetwork(ActiveWalletNetwork.TESTNET)
            .setScriptType(ActiveWalletScriptType.P2TR)
            .setDescriptor(descriptor.asStringPrivate())
            .setChangeDescriptor(changeDescriptor.asStringPrivate())
            .setRecoveryPhrase(mnemonic.asString())
            .build()
        // TODO: launch this correctly, not on the main thread
        runBlocking { activeWalletsRepository.updateActiveWallets(newWallet) }
    }

    fun loadActiveWallet(activeWallet: SingleWallet) {
        Log.i(TAG, "Loading existing wallet with descriptor: ${activeWallet.descriptor}")
        Log.i(TAG, "Loading existing wallet with change descriptor: ${activeWallet.changeDescriptor}")
        recoveryPhrase = activeWallet.recoveryPhrase
        initialize(
            descriptor = Descriptor(activeWallet.descriptor, Network.TESTNET),
            changeDescriptor = Descriptor(activeWallet.changeDescriptor, Network.TESTNET),
        )
    }

    fun recoverWallet(recoverWalletConfig: RecoverWalletConfig, activeWalletsRepository: ActiveWalletsRepository) {
        val mnemonic = Mnemonic.fromString(recoverWalletConfig.recoveryPhrase)
        val bip32ExtendedRootKey = DescriptorSecretKey(recoverWalletConfig.network, mnemonic, null)
        val descriptor: Descriptor = Descriptor.newBip84(bip32ExtendedRootKey, KeychainKind.EXTERNAL, Network.TESTNET)
        val changeDescriptor: Descriptor = Descriptor.newBip84(bip32ExtendedRootKey, KeychainKind.INTERNAL, recoverWalletConfig.network)
        initialize(
            descriptor = descriptor,
            changeDescriptor = changeDescriptor,
        )
        recoveryPhrase = mnemonic.asString()
        val newWallet: SingleWallet = SingleWallet.newBuilder()
            .setName(recoverWalletConfig.name)
            .setNetwork(recoverWalletConfig.network.intoProto())
            .setScriptType(ActiveWalletScriptType.P2TR)
            .setDescriptor(descriptor.asStringPrivate())
            .setChangeDescriptor(changeDescriptor.asStringPrivate())
            .setRecoveryPhrase(mnemonic.asString())
            .build()
        // TODO: launch this correctly, not on the main thread
        runBlocking { activeWalletsRepository.updateActiveWallets(newWallet) }
    }

    fun getRecoveryPhrase(): String {
        return recoveryPhrase
    }

    fun createTransaction(
        recipientList: MutableList<Recipient>,
        feeRate: FeeRate,
        enableRBF: Boolean,
        opReturnMsg: String?
    ): PartiallySignedTransaction {
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
            builder.addRecipient(scriptPubKey, recipient.amount)
        }
        if (enableRBF) {
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

    fun sign(psbt: PartiallySignedTransaction): Boolean {
        return wallet.sign(psbt)
    }

    fun broadcast(signedPsbt: PartiallySignedTransaction): String {
        currentBlockchainClient?.broadcast(signedPsbt.extractTx()) ?: throw IllegalStateException("Blockchain client not initialized")
        return signedPsbt.extractTx().txid()
    }

    fun getAllTransactions(): List<CanonicalTx>  = wallet.transactions()

    fun getAllTxDetails(): List<TxDetails> {
        val transactions = getAllTransactions()
        return transactions.map { tx ->
            val txid = tx.transaction.txid()
            val (sent, received) = wallet.sentAndReceived(tx.transaction)
            val fee = wallet.calculateFee(tx.transaction)
            val feeRate = wallet.calculateFeeRate(tx.transaction)
            val (confirmationBlock, confirmationTimestamp, pending) = when (val position = tx.chainPosition) {
                is ChainPosition.Unconfirmed -> Triple(null, null, true)
                is ChainPosition.Confirmed -> Triple(ConfirmationBlock(position.height), Timestamp(position.timestamp), false)
            }
            TxDetails(tx.transaction, txid, sent, received, fee, feeRate, pending, confirmationBlock, confirmationTimestamp)
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

    fun sync() {
        Log.i(TAG, "Wallet is syncing")
        val update: Update = currentBlockchainClient?.fullScan(wallet, 10u, 1u) ?: throw IllegalStateException("Blockchain client not initialized")
        wallet.applyUpdate(update)
    }

    fun getBalance(): ULong = wallet.getBalance().total

    fun getNewAddress(): AddressInfo = wallet.getAddress(AddressIndex.New)

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
}

// enum class ElectrumSettings {
//     DEFAULT,
//     CUSTOM
// }
