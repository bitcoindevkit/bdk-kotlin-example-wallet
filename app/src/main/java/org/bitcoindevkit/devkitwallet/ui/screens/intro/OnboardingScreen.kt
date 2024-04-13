/*
 * Copyright 2020-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.ui.screens.intro

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun OnboardingScreen(onFinishOnboarding: () -> Unit) {
    Text(text = "Onboarding Screen")
    Button(
        onClick = { onFinishOnboarding() }
    ) {
        Text(text = "Finish Onboarding")
    }
}
