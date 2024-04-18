/*
 * Copyright 2020-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun DevkitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        // colorScheme = devkitColors,
        // shapes = devkitShapes,
        typography = devkitTypography,
        content = content
    )
}
