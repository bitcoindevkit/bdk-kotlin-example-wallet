/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import org.bitcoindevkit.devkitwallet.domain.Repository
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.navigation.HomeNavigation
import org.bitcoindevkit.devkitwallet.navigation.CreateWalletNavigation
import org.bitcoindevkit.devkitwallet.ui.theme.DevkitTheme

private const val TAG = "DevkitWalletActivity"

class DevkitWalletActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBuildWalletButtonClicked: (WalletCreateType) -> Unit = { walletCreateType ->
            try {
                // load up a wallet either from scratch or using a BIP39 recovery phrase
                when (walletCreateType) {
                    // if we create a wallet from scratch we don't need a recovery phrase
                    is WalletCreateType.FROMSCRATCH -> Wallet.createWallet()

                    is WalletCreateType.RECOVER -> Wallet.recoverWallet(walletCreateType.recoveryPhrase)
                }
                setContent {
                    DevkitTheme {
                        HomeNavigation()
                    }
                }
            } catch(e: Throwable) {
                Log.i(TAG, "Could not build wallet: $e")
            }
        }

        if (Repository.doesWalletExist()) {
            Wallet.loadExistingWallet()
            setContent {
                DevkitTheme {
                    HomeNavigation()
                }
            }
        } else {
            setContent {
                DevkitTheme {
                    CreateWalletNavigation(onBuildWalletButtonClicked)
                }
            }
        }
    }
}

sealed class WalletCreateType {
    object FROMSCRATCH : WalletCreateType()
    class RECOVER(val recoveryPhrase: String) : WalletCreateType()
}