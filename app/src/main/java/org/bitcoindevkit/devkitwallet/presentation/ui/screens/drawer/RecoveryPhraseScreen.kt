/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.drawer

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.bitcoindevkit.devkitwallet.presentation.navigation.WalletScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.monoRegular
import org.bitcoindevkit.devkitwallet.presentation.theme.quattroRegular
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

@Composable
internal fun RecoveryPhraseScreen(recoveryPhrase: List<String>, navController: NavController) {
    val (currentIndex, setCurrentIndex) = remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Your Recovery Phrase",
                navigation = { navController.navigate(WalletScreen) }
            )
        },
        containerColor = DevkitWalletColors.primary
    ) { paddingValues ->
        Crossfade(
            modifier = Modifier.padding(paddingValues),
            targetState = currentIndex,
            label = "",
            animationSpec = tween(
                durationMillis = 1000,
                delayMillis = 200,
            )
        ) { screen ->
            when (screen) {
                0 -> WarningText(setCurrentIndex = setCurrentIndex)
                1 -> RecoveryPhrase(recoveryPhrase = recoveryPhrase)
            }
        }
    }
}

@Composable
fun WarningText(setCurrentIndex: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "The next screen will show your recovery phrase. Make sure no one else is looking at your screen.",
            color = DevkitWalletColors.white,
            fontFamily = quattroRegular
        )
        Spacer(modifier = Modifier.padding(16.dp))
        NeutralButton(
            text = "See my recovery phrase",
            enabled = true
        ) { setCurrentIndex(1) }
    }
}

@Composable
fun RecoveryPhrase(recoveryPhrase: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 32.dp)
    ) {
        Text(
            text = "Write down your recovery phrase and keep it in a safe place.",
            color = DevkitWalletColors.white,
            fontFamily = quattroRegular
        )
        Spacer(modifier = Modifier.padding(16.dp))
        recoveryPhrase.forEachIndexed { index, item ->
            val space = if (index < 9) "  " else " "
            Text(
                text = "${index + 1}.$space$item",
                color = DevkitWalletColors.white,
                fontFamily = monoRegular
            )
        }
    }
}

@Preview(device = Devices.PIXEL_4, showBackground = true)
@Composable
internal fun PreviewRecoveryPhraseScreen() {
    RecoveryPhraseScreen(
        listOf(
            "word1",
            "word2",
            "word3",
            "word4",
            "word5",
            "word6",
            "word7",
            "word8",
            "word9",
            "word10",
            "word11",
            "word12"
        ),
        rememberNavController()
    )
}
