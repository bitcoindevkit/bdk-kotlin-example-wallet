/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
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
import androidx.compose.material3.DrawerState
import org.bitcoindevkit.devkitwallet.domain.Wallet
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.RBFScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.ReceiveScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.SendScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.TransactionScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.TransactionHistoryScreen
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet.WalletHomeScreen
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.AddressViewModel
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.SendViewModel
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.WalletViewModel

@Composable
fun WalletNavigation(
    drawerState: DrawerState,
    activeWallet: Wallet
) {
    val navController: NavHostController = rememberNavController()
    val animationDuration = 400

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
    ) {

        composable(
            route = Screen.HomeScreen.route,
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
        ) { WalletHomeScreen(navController, drawerState, WalletViewModel(activeWallet)) }

        composable(
            route = Screen.ReceiveScreen.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            }
        ) { ReceiveScreen(navController, AddressViewModel(activeWallet)) }

        composable(
            route = Screen.SendScreen.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            }
        ) { SendScreen(navController, SendViewModel(activeWallet)) }

        composable(
            route = "${Screen.RBFScreen.route}/txid={txid}",
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            }
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("txid")?.let {
                RBFScreen(navController, backStackEntry.arguments?.getString("txid"))
            }
        }

        composable(
            route = Screen.TransactionsScreen.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            }
        ) { TransactionHistoryScreen(navController, activeWallet) }

        composable(
            route = "${Screen.TransactionScreen.route}/txid={txid}",
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(animationDuration))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(animationDuration))
            }
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("txid")?.let {
                TransactionScreen(navController, backStackEntry.arguments?.getString("txid"))
            }
        }
    }
}
