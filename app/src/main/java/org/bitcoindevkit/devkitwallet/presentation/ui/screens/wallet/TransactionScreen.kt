/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.presentation.navigation.RbfScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.monoRegular
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar

@Composable
internal fun TransactionScreen(txid: String?, navController: NavController) {
    // val transaction = getTransaction(txid = txid)
    // if (transaction == null) {
    //     navController.popBackStack()
    // }
    // val transactionDetail = getTransactionDetails(transaction = transaction!!)

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Transaction Details",
                navigation = { navController.navigateUp() }
            )
        },
        containerColor = DevkitWalletColors.primary
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(DevkitWalletColors.primary)
                .padding(paddingValues)
        ) {
            val (screenTitle, transactions, bottomButton) = createRefs()

            Column(
                modifier = Modifier
                    .constrainAs(screenTitle) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }.padding(top = 70.dp)
            ) {
                Text(
                    text = "Transaction",
                    color = DevkitWalletColors.white,
                    fontSize = 28.sp,
                    fontFamily = monoRegular,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                // Text(
                //     text = transactionTitle(transaction = transaction),
                //     color = DevkitWalletColors.white,
                //     fontSize = 14.sp,
                //     textAlign = TextAlign.Center,
                //     modifier = Modifier.padding(horizontal = 16.dp)
                // )
            }

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .constrainAs(transactions) {
                        top.linkTo(screenTitle.bottom)
                        bottom.linkTo(bottomButton.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    }
            ) {
                // items(transactionDetail) {
                //     Row(
                //         modifier = Modifier
                //             .fillMaxWidth()
                //             .padding(all = 16.dp)
                //     ) {
                //         Text(
                //             text = "${it.first} :",
                //             fontSize = 16.sp,
                //             color = DevkitWalletColors.white,
                //         )
                //         Text(
                //             text = it.second,
                //             fontSize = 16.sp,
                //             textAlign = TextAlign.End,
                //             color = DevkitWalletColors.white,
                //             modifier = Modifier.fillMaxWidth()
                //         )
                //     }
                // }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                    .constrainAs(bottomButton) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                TransactionDetailButton(
                    content = "increase fees",
                    navController = navController,
                    txid = txid
                )
            }
        }
    }
}

@Composable
fun TransactionDetailButton(content: String, navController: NavController, txid: String?) {
    Button(
        onClick = {
            when (content) {
                "increase fees" -> {
                    navController.navigate(RbfScreen(txid!!))
                }
                "back to transaction list" -> {
                    navController.navigateUp()
                }
            }
        },
        colors = ButtonDefaults.buttonColors(DevkitWalletColors.secondary),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = content,
            fontSize = 14.sp,
            fontFamily = monoRegular,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
        )
    }
}

// fun getTransactionDetails(transaction: TransactionDetails): List<Pair<String, String>> {
//     val transactionDetails = mutableListOf<Pair<String, String>>()
//
//     if (transaction.confirmationTime != null) {
//         transactionDetails.add(Pair("Status", "Confirmed"))
//         transactionDetails.add(Pair("Timestamp", transaction.confirmationTime!!.timestamp.timestampToString()))
//         transactionDetails.add(Pair("Received", (if (transaction.received < transaction.sent) 0 else transaction.received).toString()))
//         transactionDetails.add(Pair("Sent", (if (transaction.sent < transaction.received) 0 else transaction.sent - transaction.received - transaction.fee!!).toString()))
//         transactionDetails.add(Pair("Fees", transaction.fee.toString()))
//         transactionDetails.add(Pair("Block", transaction.confirmationTime!!.height.toString()))
//     } else {
//         transactionDetails.add(Pair("Status", "Pending"))
//         transactionDetails.add(Pair("Timestamp", "Pending"))
//         transactionDetails.add(Pair("Received", (if (transaction.received < transaction.sent) 0 else transaction.received).toString()))
//         transactionDetails.add(Pair("Sent", (if (transaction.sent < transaction.received) 0 else transaction.sent - transaction.received - transaction.fee!!).toString()))
//         transactionDetails.add(Pair("Fees", transaction.fee.toString()))
//     }
//     return transactionDetails
// }
//
// fun transactionTitle(transaction: TransactionDetails): String {
//     return transaction.txid
// }
//
// fun getTransaction(txid: String?): TransactionDetails? {
//     if (txid.isNullOrEmpty()) {
//         return null
//     }
//     return Wallet.getTransaction(txid = txid)
// }
