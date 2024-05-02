/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.graphics.createBitmap
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.presentation.navigation.Screen
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import org.bitcoindevkit.devkitwallet.presentation.theme.monoRegular
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.ReceiveScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.AddressViewModel

private const val TAG = "ReceiveScreen"

@Composable
internal fun ReceiveScreen(
    navController: NavController,
    addressViewModel: AddressViewModel
) {
    Log.i(TAG, "We are recomposing the ReceiveScreen")
    val state = addressViewModel.state
    val onAction = addressViewModel::onAction

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Receive Address",
                navigation = { navController.navigate(Screen.HomeScreen.route) }
            )
        }
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(DevkitWalletColors.primary)
        ) {
            val (QRCode, bottomButtons) = createRefs()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(QRCode) {
                    top.linkTo(parent.top)
                    bottom.linkTo(bottomButtons.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
            ) {
                val QR: ImageBitmap? = state.address?.let { addressToQR(it) }
                Log.i("ReceiveScreen", "New receive address is ${state.address}")
                if (QR != null) {
                    Image(
                        bitmap = QR,
                        contentDescription = "Bitcoindevkit website QR code",
                        Modifier.size(250.dp)
                    )
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    SelectionContainer {
                        Text(
                            text = state.address,
                            fontFamily = monoRegular,
                            color = DevkitWalletColors.white
                        )
                    }
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "index: ${state.addressIndex}",
                        fontFamily = monoRegular,
                        color = DevkitWalletColors.white
                    )
                }
            }

            Column(
                Modifier
                    .constrainAs(bottomButtons) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(bottom = 24.dp)
            ) {
                Button(
                    onClick = { onAction(ReceiveScreenAction.UpdateAddress) },
                    colors = ButtonDefaults.buttonColors(DevkitWalletColors.secondary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "generate address",
                        fontSize = 14.sp,
                        fontFamily = monoRegular,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                    )
                }
            }
        }
    }
}

private fun addressToQR(address: String): ImageBitmap? {
    Log.i(TAG, "We are generating the QR code for address $address")
    try {
        val qrCodeWriter: QRCodeWriter = QRCodeWriter()
        val bitMatrix: BitMatrix = qrCodeWriter.encode(address, BarcodeFormat.QR_CODE, 1000, 1000)
        val bitMap = createBitmap(1000, 1000)
        for (x in 0 until 1000) {
            for (y in 0 until 1000) {
                // DevkitWalletColors.primaryDark for dark and DevkitWalletColors.white for light
                bitMap.setPixel(x, y, if (bitMatrix[x, y]) 0xff203b46.toInt() else 0xffffffff.toInt())
            }
        }
        return bitMap.asImageBitmap()
    } catch (e: Throwable) {
        Log.i("ReceiveScreen", "Error with QRCode generation, $e")
    }
    return null
}

// @Preview(device = Devices.PIXEL_4, showBackground = true)
// @Composable
// internal fun PreviewReceiveScreen() {
//     ReceiveScreen(rememberNavController())
// }
