/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.bitcoindevkit.devkitwallet.data.ActiveWalletNetwork
import org.bitcoindevkit.devkitwallet.data.ActiveWalletScriptType
import org.bitcoindevkit.devkitwallet.data.ActiveWallets
import org.bitcoindevkit.devkitwallet.data.ActiveWalletsSerializer
import org.bitcoindevkit.devkitwallet.data.SingleWallet
import org.bitcoindevkit.devkitwallet.domain.ActiveWalletsRepository
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.navigation.HomeNavigation
import org.bitcoindevkit.devkitwallet.navigation.CreateWalletNavigation
import org.bitcoindevkit.devkitwallet.ui.theme.DevkitTheme

private const val TAG = "DevkitWalletActivity"
private val Context.activeWalletsStore: DataStore<ActiveWallets> by dataStore(
    fileName = "wallets_preferences.pb",
    serializer = ActiveWalletsSerializer
)

class DevkitWalletActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activeWalletsRepository = ActiveWalletsRepository(activeWalletsStore)
        lifecycleScope.launch {
            val activeWallets =
                async { activeWalletsRepository.fetchActiveWallets().walletsList }.await()

            val onBuildWalletButtonClicked: (WalletCreateType) -> Unit = { walletCreateType ->
                try {
                    when (walletCreateType) {
                        is WalletCreateType.FROMSCRATCH -> Wallet.createWallet(
                            activeWalletsRepository
                        )
                        is WalletCreateType.LOADEXISTING -> Wallet.loadActiveWallet(walletCreateType.activeWallet)
                        is WalletCreateType.RECOVER -> Wallet.recoverWallet(
                            walletCreateType.recoverWalletConfig,
                            activeWalletsRepository
                        )
                    }
                    setContent {
                        DevkitTheme {
                            HomeNavigation()
                        }
                    }
                } catch (e: Throwable) {
                    Log.i(TAG, "Could not build wallet: $e")
                }
            }

            setContent {
                DevkitTheme {
                    CreateWalletNavigation(onBuildWalletButtonClicked, activeWallets)
                }
            }
        }
    }
}

sealed class WalletCreateType {
    data class FROMSCRATCH(val newWalletConfig: NewWalletConfig) : WalletCreateType()
    data class LOADEXISTING(val activeWallet: SingleWallet) : WalletCreateType()
    data class RECOVER(val recoverWalletConfig: RecoverWalletConfig) : WalletCreateType()
}

data class NewWalletConfig(
    val name: String,
    val network: ActiveWalletNetwork,
    val scriptType: ActiveWalletScriptType,
)

data class RecoverWalletConfig(
    val name: String,
    val network: ActiveWalletNetwork,
    val scriptType: ActiveWalletScriptType,
    val recoveryPhrase: String,
)
