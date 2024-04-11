/*
 * Copyright 2020-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults.colors
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.ui.Screen
import org.bitcoindevkit.devkitwallet.ui.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.navigation.WalletNavigation
import org.bitcoindevkit.devkitwallet.ui.theme.jetBrainsMonoLight
import org.bitcoindevkit.devkitwallet.R

@OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
internal fun WalletRoot(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val items = listOf(Icons.Default.Favorite, Icons.Default.Face, Icons.Default.Email, Icons.Default.Face)
    val selectedItem = remember { mutableStateOf(items[0]) }

    val navigationItemColors = colors(
        selectedContainerColor = DevkitWalletColors.primary,
        unselectedContainerColor = DevkitWalletColors.primary,
        selectedTextColor = DevkitWalletColors.white,
        unselectedTextColor = DevkitWalletColors.white
    )

    ModalNavigationDrawer (
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    Modifier
                        .background(color = DevkitWalletColors.secondary)
                        .height(300.dp)
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_testnet_logo),
                        contentDescription = "Bitcoin testnet logo",
                        Modifier
                            .size(90.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = "BDK Android Sample Wallet",
                        color = DevkitWalletColors.white,
                        fontFamily = jetBrainsMonoLight,
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        "Version 0.1.0",
                        color = DevkitWalletColors.white,
                        fontFamily = jetBrainsMonoLight,
                    )
                }
                Column(
                    Modifier.fillMaxHeight().background(color = DevkitWalletColors.primary)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationDrawerItem(
                        label = { DrawerItemLabel("About") },
                        selected = items[0] == selectedItem.value,
                        onClick = { navController.navigate(Screen.AboutScreen.route) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = navigationItemColors
                    )
                    NavigationDrawerItem(
                        label = { DrawerItemLabel("Recovery Phrase") },
                        selected = items[1] == selectedItem.value,
                        onClick = { navController.navigate(Screen.RecoveryPhraseScreen.route) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = navigationItemColors
                    )
                    NavigationDrawerItem(
                        label = { DrawerItemLabel("Custom Blockchain Client") },
                        selected = items[2] == selectedItem.value,
                        onClick = { navController.navigate(Screen.CustomBlockchainClient.route) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = navigationItemColors
                    )
                }
            }
        },
        content = {
            WalletNavigation(
                drawerState = drawerState,
            )
        }
    )
}

@Composable
fun DrawerItemLabel(text: String) {
    Text(
        text = text,
        fontFamily = jetBrainsMonoLight,
    )
}
