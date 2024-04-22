/*
 * Copyright 2020-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.WalletChoiceScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.RecoverWalletScreen
import org.bitcoindevkit.devkitwallet.presentation.WalletCreateType
import org.bitcoindevkit.devkitwallet.data.SingleWallet
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.ActiveWalletsScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.CreateNewWalletScreen

@Composable
fun CreateWalletNavigation(
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit,
    activeWallets: List<SingleWallet>,
) {
    val navController: NavHostController = rememberNavController()
    val animationDuration = 400

    NavHost(
        navController = navController,
        startDestination = Screen.WalletChoiceScreen.route,
    ) {

        composable(
            route = Screen.WalletChoiceScreen.route,
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
        ) { WalletChoiceScreen(navController = navController) }

        composable(
            route = Screen.ActiveWalletsScreen.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            }
        ) { ActiveWalletsScreen(activeWallets = activeWallets, navController = navController, onBuildWalletButtonClicked) }

        composable(
            route = Screen.CreateNewWalletScreen.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            }
        ) { CreateNewWalletScreen(navController = navController, onBuildWalletButtonClicked) }

        composable(
            route = Screen.WalletRecoveryScreen.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            }
        ) { RecoverWalletScreen(navController = navController, onBuildWalletButtonClicked) }
    }
}
