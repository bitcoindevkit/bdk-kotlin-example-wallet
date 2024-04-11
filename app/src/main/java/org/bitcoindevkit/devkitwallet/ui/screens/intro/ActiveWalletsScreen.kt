/*
 * Copyright 2020-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.ui.screens.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.ui.theme.DevkitWalletColors

@Composable
internal fun ActiveWalletsScreen(
    navController: NavController,
) {
    Scaffold(
        topBar = {
            SecondaryScreensAppBar(title = "Choose a Wallet", navigation = { navController.popBackStack() })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = DevkitWalletColors.primary),
        ) {
            Text(text = "Your Active Wallets")
        }
    }
}
