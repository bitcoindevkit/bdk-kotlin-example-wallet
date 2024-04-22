/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation

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
import org.bitcoindevkit.devkitwallet.data.ActiveWallets
import org.bitcoindevkit.devkitwallet.data.ActiveWalletsSerializer
import org.bitcoindevkit.devkitwallet.data.IntroDone
import org.bitcoindevkit.devkitwallet.data.IntroDoneSerializer
import org.bitcoindevkit.devkitwallet.data.NewWalletConfig
import org.bitcoindevkit.devkitwallet.data.RecoverWalletConfig
import org.bitcoindevkit.devkitwallet.data.SingleWallet
import org.bitcoindevkit.devkitwallet.domain.ActiveWalletsRepository
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.navigation.HomeNavigation
import org.bitcoindevkit.devkitwallet.presentation.navigation.CreateWalletNavigation
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.OnboardingScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitTheme

private const val TAG = "DevkitWalletActivity"
private val Context.activeWalletsStore: DataStore<ActiveWallets> by dataStore(
    fileName = "wallets_preferences.pb",
    serializer = ActiveWalletsSerializer
)
private val Context.introDoneStore: DataStore<IntroDone> by dataStore(
    fileName = "intro_done.pb",
    serializer = IntroDoneSerializer
)

class DevkitWalletActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activeWalletsRepository = ActiveWalletsRepository(activeWalletsStore, introDoneStore)

        lifecycleScope.launch {
            val activeWallets = async {
                activeWalletsRepository.fetchActiveWallets().walletsList
            }.await()

            val onboardingDone = async {
                activeWalletsRepository.fetchIntroDone().introDone
            }.await()

            val onBuildWalletButtonClicked: (WalletCreateType) -> Unit = { walletCreateType ->
                try {
                    val activeWallet = when (walletCreateType) {
                        is WalletCreateType.FROMSCRATCH -> Wallet.createWallet(
                            newWalletConfig = walletCreateType.newWalletConfig,
                            activeWalletsRepository = activeWalletsRepository,
                            internalAppFilesPath = filesDir.absolutePath
                        )
                        is WalletCreateType.RECOVER -> throw NotImplementedError("Recover wallet not implemented")
                        is WalletCreateType.LOADEXISTING -> throw NotImplementedError("Load existing wallet not implemented")
                        // is WalletCreateType.LOADEXISTING -> Wallet.loadActiveWallet(walletCreateType.activeWallet)
                        // is WalletCreateType.RECOVER -> Wallet.recoverWallet(
                        //     walletCreateType.recoverWalletConfig,
                        //     activeWalletsRepository
                        // )
                    }
                    setContent {
                        DevkitTheme {
                            HomeNavigation(activeWallet)
                        }
                    }
                } catch (e: Throwable) {
                    Log.i(TAG, "Could not build wallet: $e")
                }
            }

            val onFinishOnboarding: () -> Unit = {
                lifecycleScope.launch { activeWalletsRepository.setIntroDone() }
                setContent {
                    DevkitTheme {
                        CreateWalletNavigation(onBuildWalletButtonClicked, activeWallets)
                    }
                }
            }

            setContent {
                if (!onboardingDone) {
                    OnboardingScreen(onFinishOnboarding)
                } else {
                    DevkitTheme {
                        CreateWalletNavigation(onBuildWalletButtonClicked, activeWallets)
                    }
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
