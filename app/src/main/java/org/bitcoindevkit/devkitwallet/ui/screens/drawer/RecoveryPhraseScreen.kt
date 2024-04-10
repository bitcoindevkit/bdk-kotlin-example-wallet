/*
 * Copyright 2020-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.ui.screens.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.bitcoindevkit.devkitwallet.domain.Repository
import org.bitcoindevkit.devkitwallet.ui.Screen
import org.bitcoindevkit.devkitwallet.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.ui.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.ui.theme.jetBrainsMonoLight

@Composable
internal fun RecoveryPhraseScreen(navController: NavController) {

    val seedPhrase: String = Repository.getMnemonic()
    val wordList: List<String> = seedPhrase.split(" ")

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Your Recovery Phrase",
                navigation = { navController.navigate(Screen.WalletScreen.route) }
            )
        },
        containerColor = DevkitWalletColors.primary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = 32.dp)
        ) {
            wordList.forEachIndexed { index, item ->
                Text(
                    text = "${index + 1}. $item",
                    modifier = Modifier.weight(weight = 1F),
                    color = DevkitWalletColors.white,
                    fontFamily = jetBrainsMonoLight
                )
            }
        }
    }
}

@Preview(device = Devices.PIXEL_4, showBackground = true)
@Composable
internal fun PreviewRecoveryPhraseScreen() {
    RecoveryPhraseScreen(rememberNavController())
}
