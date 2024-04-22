/*
 * Copyright 2020-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.data.ActiveWalletScriptType
import org.bitcoindevkit.devkitwallet.data.NewWalletConfig
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.jetBrainsMonoLight

@Composable
internal fun CreateNewWalletScreen(
    navController: NavController,
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit
) {
    Scaffold(
        topBar = {
            SecondaryScreensAppBar(title = "Create a New Wallet", navigation = { navController.popBackStack() })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = DevkitWalletColors.primary),
        ) {
            val walletName: MutableState<String> = remember { mutableStateOf("") }
            var selectedNetwork: Network by remember { mutableStateOf(Network.TESTNET) }
            val network = listOf(Network.TESTNET)
            var selectedScriptType: ActiveWalletScriptType by remember { mutableStateOf(ActiveWalletScriptType.P2TR) }
            val scriptTypes = listOf(ActiveWalletScriptType.P2TR)

            OutlinedTextField(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                value = walletName.value,
                onValueChange = { walletName.value = it },
                label = {
                    Text(
                        text = "Wallet Name",
                        color = DevkitWalletColors.white,
                    )
                },
                singleLine = true,
                textStyle = TextStyle(fontFamily = jetBrainsMonoLight, color = DevkitWalletColors.white),
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = DevkitWalletColors.accent1,
                    focusedBorderColor = DevkitWalletColors.accent1,
                    unfocusedBorderColor = DevkitWalletColors.white,
                ),
            )

            network.forEach {
                RadioButtonWithLabel(
                    label = it.name,
                    isSelected = selectedNetwork == it,
                    onSelect = { selectedNetwork = it }
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))

            scriptTypes.forEach {
                RadioButtonWithLabel(
                    label = it.name,
                    isSelected = selectedScriptType == it,
                    onSelect = { selectedScriptType = it }
                )
            }

            NeutralButton(
                text = "Create Wallet",
                enabled = true,
                onClick = {
                    val newWalletConfig = NewWalletConfig(
                        name = walletName.value,
                        network = selectedNetwork,
                        scriptType = selectedScriptType
                    )
                    onBuildWalletButtonClicked(
                        WalletCreateType.FROMSCRATCH(newWalletConfig)
                    )
                }
            )
        }
    }
}

@Composable
fun RadioButtonWithLabel(label: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(modifier = Modifier.padding(8.dp)) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect
        )
        Text(
            text = label,
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable(onClick = onSelect)
        )
    }
}
