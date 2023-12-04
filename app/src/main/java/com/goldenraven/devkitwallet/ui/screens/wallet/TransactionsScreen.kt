/*
 * Copyright 2020-2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.screens.wallet

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.goldenraven.devkitwallet.domain.Wallet
import com.goldenraven.devkitwallet.ui.Screen
import com.goldenraven.devkitwallet.ui.components.SecondaryScreensAppBar
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors
import com.goldenraven.devkitwallet.ui.theme.jetBrainsMonoLight
import com.goldenraven.devkitwallet.utils.timestampToString
import org.bitcoindevkit.TransactionDetails

private const val TAG = "TransactionsScreen"

@Composable
internal fun TransactionsScreen(navController: NavController) {
    val allTransactions: List<TransactionDetails> = Wallet.getAllTransactions()
    val (confirmedTransactions, unconfirmedTransactions) = sortTransactions(allTransactions)

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Transactions History",
                navigation = { navController.navigate(Screen.HomeScreen.route) }
            )
        }
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(DevkitWalletColors.primaryLight)
                .padding(paddingValues)
        ) {
            val (screenTitle, transactions, bottomButton) = createRefs()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(transactions) {
                    top.linkTo(screenTitle.bottom)
                    bottom.linkTo(bottomButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(DevkitWalletColors.primary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Pending",
                        fontSize = 18.sp,
                        fontFamily = jetBrainsMonoLight,
                        color = DevkitWalletColors.white
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(120.dp)
                        .background(DevkitWalletColors.primary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(state = scrollState)
                    ) {
                        if (unconfirmedTransactions.isNotEmpty()) {
                            unconfirmedTransactions.forEach {
                                Text(
                                    text = pendingTransactionsItem(it),
                                    fontSize = 12.sp,
                                    fontFamily = jetBrainsMonoLight,
                                    color = DevkitWalletColors.white,
                                    modifier = Modifier
                                        .padding(all = 4.dp)
                                        .clickable {
                                            viewTransaction(
                                                navController = navController,
                                                txid = it.txid
                                            )
                                        }
                                )
                            }
                        } else {
                            Text(
                                text = "No Pending Transactions",
                                fontSize = 12.sp,
                                fontFamily = jetBrainsMonoLight,
                                color = DevkitWalletColors.white,
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(DevkitWalletColors.primary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Confirmed",
                        fontSize = 18.sp,
                        fontFamily = jetBrainsMonoLight,
                        color = DevkitWalletColors.white
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(200.dp)
                        .background(DevkitWalletColors.primary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(state = scrollState)
                    ) {
                        if (confirmedTransactions.isNotEmpty()) {
                            confirmedTransactions.forEach {
                                Text(
                                    text = confirmedTransactionsItem(it),
                                    fontSize = 12.sp,
                                    fontFamily = jetBrainsMonoLight,
                                    color = DevkitWalletColors.white,
                                    modifier = Modifier
                                        .padding(all = 4.dp)
                                        .clickable {
                                            viewTransaction(
                                                navController = navController,
                                                txid = it.txid
                                            )
                                        }
                                )
                            }
                        } else {
                            Text(
                                text = "No Confirmed Transactions",
                                fontSize = 12.sp,
                                fontFamily = jetBrainsMonoLight,
                                color = DevkitWalletColors.white
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun viewTransaction(navController: NavController, txid: String) {
    navController.navigate("${Screen.TransactionScreen.route}/txid=$txid")
}

private fun sortTransactions(transactions: List<TransactionDetails>): Transactions {
    val confirmedTransactions = mutableListOf<TransactionDetails>()
    val unconfirmedTransactions = mutableListOf<TransactionDetails>()
    transactions.forEach { tx ->
        if (tx.confirmationTime != null) confirmedTransactions.add(tx) else unconfirmedTransactions.add(tx)
    }
    return Transactions(
        confirmedTransactions = confirmedTransactions,
        unconfirmedTransactions = unconfirmedTransactions
    )
}

private fun pendingTransactionsItem(transaction: TransactionDetails): String {
    return buildString {
        Log.i(TAG, "Pending transaction list item: $transaction")
        appendLine("Timestamp: Pending")
        appendLine("Received: ${transaction.received}")
        appendLine("Sent: ${transaction.sent}")
        appendLine("Fees: ${transaction.fee}")
        appendLine("Txid: ${transaction.txid}")
    }
}

private fun confirmedTransactionsItem(transaction: TransactionDetails): String {
    return buildString {
        Log.i(TAG, "Transaction list item: $transaction")
        appendLine("Timestamp: ${transaction.confirmationTime!!.timestamp.timestampToString()}")
        appendLine("Received: ${transaction.received}")
        appendLine("Sent: ${transaction.sent}")
        appendLine("Fees: ${transaction.fee}")
        appendLine("Block: ${transaction.confirmationTime!!.height}")
        appendLine("Txid: ${transaction.txid}")
    }
}

data class Transactions(
    val confirmedTransactions: List<TransactionDetails>,
    val unconfirmedTransactions: List<TransactionDetails>
)

// @Preview(device = Devices.PIXEL_4, showBackground = true)
// @Composable
// internal fun PreviewTransactionsScreen() {
//     TransactionsScreen(rememberNavController())
// }
