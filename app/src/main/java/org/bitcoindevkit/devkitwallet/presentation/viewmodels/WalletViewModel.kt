/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import org.bitcoindevkit.devkitwallet.domain.Wallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bitcoindevkit.Warning
import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.KyotoNodeStatus
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.WalletScreenState

private const val TAG = "WalletViewModel"

internal class WalletViewModel(
    private val wallet: Wallet
) : ViewModel() {

    var state: WalletScreenState by mutableStateOf(WalletScreenState())
        private set

    private val kyotoCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun onAction(action: WalletScreenAction) {
        when (action) {
            WalletScreenAction.SwitchUnit     -> switchUnit()
            WalletScreenAction.UpdateBalance  -> updateBalance()
            WalletScreenAction.StartKyotoNode -> startKyotoNode()
            WalletScreenAction.StopKyotoNode  -> stopKyotoNode()
            WalletScreenAction.StartKyotoSync -> startKyotoSync()
            WalletScreenAction.ClearSnackbar  -> clearSnackbar()
        }
    }

    private fun showSnackbar(message: String) {
        state = state.copy(snackbarMessage = message)
    }

    private fun clearSnackbar() {
        state = state.copy(snackbarMessage = null)
    }

    private fun switchUnit() {
        state = when (state.unit) {
            CurrencyUnit.Bitcoin -> state.copy(unit = CurrencyUnit.Satoshi)
            CurrencyUnit.Satoshi -> state.copy(unit = CurrencyUnit.Bitcoin)
        }
    }

    private fun updateLatestBlock(blockHeight: UInt) {
        state = state.copy(latestBlock = blockHeight)
    }

    private fun updateBalance() {
        viewModelScope.launch(Dispatchers.IO) {
            val newBalance = wallet.getBalance()
            Log.i("Kyoto", "New balance: $newBalance")
            state = state.copy(balance = newBalance)
            Log.i("Kyoto", "New state object: $state")
        }
    }

    private fun startKyotoNode() {
        Log.i("Kyoto", "Starting Kyoto node")
        wallet.startKyotoNode()
        state = state.copy(kyotoNodeStatus = KyotoNodeStatus.Running)
    }

    private fun startKyotoSync() {
        Log.i("Kyoto", "Starting Kyoto sync")

        kyotoCoroutineScope.launch {
            while (wallet.kyotoClient != null) {
                val update = wallet.kyotoClient?.update()
                if (update == null) {
                    Log.i("Kyoto", "UPDATE: Update is null")
                } else {
                    Log.i("Kyoto", "UPDATE: Applying an update to the wallet")
                    wallet.applyUpdate(update)
                }
                updateBalance()
            }
        }

        kyotoCoroutineScope.launch {
            while (wallet.kyotoClient != null) {
                val nextLog: org.bitcoindevkit.Log = wallet.kyotoClient!!.nextLog()
                Log.i("Kyoto", "LOG: $nextLog")
            }
        }

        kyotoCoroutineScope.launch {
            while (wallet.kyotoClient != null) {
                val nextWarning: Warning = wallet.kyotoClient!!.nextWarning()
                Log.i("Kyoto", "WARNING: $nextWarning")
            }
        }
    }

    private fun stopKyotoNode() {
        Log.i("Kyoto", "Stopping Kyoto node")
        viewModelScope.launch {
            wallet.stopKyotoNode()
        }
        state = state.copy(kyotoNodeStatus = KyotoNodeStatus.Stopped)
    }
}
