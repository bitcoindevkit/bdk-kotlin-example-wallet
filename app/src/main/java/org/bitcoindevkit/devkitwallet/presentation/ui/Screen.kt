/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui

sealed class Screen(val route: String) {
    // create wallet screens
    data object WalletChoiceScreen : Screen("wallet_choice_screen")
    data object ActiveWalletsScreen : Screen("active_wallets_screen")
    data object CreateNewWalletScreen : Screen("create_new_wallet_screen")
    data object WalletRecoveryScreen : Screen("wallet_recovery_screen")

    // home screens
    data object WalletScreen : Screen("wallet_screen")
    data object AboutScreen : Screen("about_screen")
    data object RecoveryPhraseScreen : Screen("recovery_phrase_screen")
    data object CustomBlockchainClient : Screen("custom_client_screen")

    // wallet screens
    data object HomeScreen : Screen("home_screen")
    data object SendScreen : Screen("send_screen")
    data object ReceiveScreen : Screen("receive_screen")
    data object RBFScreen : Screen("rbf_screen")
    data object TransactionsScreen : Screen("transactions_screen")
    data object TransactionScreen : Screen("transaction_screen")
}
