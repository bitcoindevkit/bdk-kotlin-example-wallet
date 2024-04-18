package org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi

import org.bitcoindevkit.devkitwallet.domain.CurrencyUnit

data class WalletScreenState(
    val balance: ULong = 0u,
    val syncing: Boolean = false,
    val unit: CurrencyUnit = CurrencyUnit.Bitcoin
)

sealed interface WalletScreenAction {
    data object UpdateBalance : WalletScreenAction
    data object SwitchUnit : WalletScreenAction
}
