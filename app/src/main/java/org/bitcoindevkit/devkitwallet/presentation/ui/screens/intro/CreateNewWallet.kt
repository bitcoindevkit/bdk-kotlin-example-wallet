/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.data.ActiveWalletScriptType
import org.bitcoindevkit.devkitwallet.data.NewWalletConfig
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.monoRegular
import java.util.Locale

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

        ConstraintLayout(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(color = DevkitWalletColors.primary)
        ) {
            val (choices, button) = createRefs()

            val walletName: MutableState<String> = remember { mutableStateOf("") }
            var selectedNetwork: Network by remember { mutableStateOf(Network.TESTNET) }
            val network = listOf(Network.TESTNET, Network.REGTEST)
            var selectedScriptType: ActiveWalletScriptType by remember { mutableStateOf(ActiveWalletScriptType.P2TR) }
            val scriptTypes = listOf(ActiveWalletScriptType.P2TR)

            Column(
                modifier = Modifier
                    .constrainAs(choices) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxSize()
                    .background(color = DevkitWalletColors.primary)
                    .padding(16.dp)
                    .padding(paddingValues),
            ) {

                OutlinedTextField(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    value = walletName.value,
                    onValueChange = { walletName.value = it },
                    label = {
                        Text(
                            text = "Wallet Name",
                            color = DevkitWalletColors.white,
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = monoRegular, color = DevkitWalletColors.white),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = DevkitWalletColors.accent1,
                        focusedBorderColor = DevkitWalletColors.accent1,
                        unfocusedBorderColor = DevkitWalletColors.white,
                    ),
                )

                Spacer(modifier = Modifier.padding(24.dp))
                Text(
                    text = "Network",
                    color = DevkitWalletColors.white
                )

                network.forEach {
                    RadioButtonWithLabel(
                        label = it.name.lowercase().replaceFirstChar { char -> char.uppercase() },
                        isSelected = selectedNetwork == it,
                        onSelect = { selectedNetwork = it }
                    )
                }

                Spacer(modifier = Modifier.padding(24.dp))
                Text(text = "Script Type", color = DevkitWalletColors.white)

                scriptTypes.forEach {
                    RadioButtonWithLabel(
                        label = it.name,
                        isSelected = selectedScriptType == it,
                        onSelect = { selectedScriptType = it }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .constrainAs(button) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(bottom = 24.dp)
            ) {
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
}

@Composable
fun RadioButtonWithLabel(label: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .selectable(
                selected = isSelected,
                onClick = onSelect
            )
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = DevkitWalletColors.accent1,
                unselectedColor = DevkitWalletColors.accent2
            )
        )
        Text(
            text = label,
            modifier = Modifier
                .clickable(onClick = onSelect)
        )
    }
}
