/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import org.bitcoindevkit.devkitwallet.ui.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.ui.theme.jetBrainsMonoRegular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SecondaryScreensAppBar(
    title: String,
    navigation: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = DevkitWalletColors.white,
                fontSize = 18.sp,
                fontFamily = jetBrainsMonoRegular
            )
        },
        navigationIcon = {
            IconButton(onClick = navigation) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = DevkitWalletColors.white
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DevkitWalletColors.primaryDark,
        )
    )
}
