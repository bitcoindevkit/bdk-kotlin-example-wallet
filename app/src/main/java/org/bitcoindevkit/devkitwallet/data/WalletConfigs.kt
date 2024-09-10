/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.data

import org.rustbitcoin.bitcoin.Network

data class NewWalletConfig(
    val name: String,
    val network: Network,
    val scriptType: ActiveWalletScriptType,
)

data class RecoverWalletConfig(
    val name: String,
    val network: Network,
    val scriptType: ActiveWalletScriptType,
    val recoveryPhrase: String,
)
