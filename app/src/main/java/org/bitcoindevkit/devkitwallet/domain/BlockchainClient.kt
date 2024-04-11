/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.domain

import org.bitcoindevkit.Transaction
import org.bitcoindevkit.EsploraClient as BdkEsploraClient
import org.bitcoindevkit.Update
import org.bitcoindevkit.Wallet as BdkWallet

interface BlockchainClient {
    fun fullScan(wallet: BdkWallet, stopGap: ULong, parallelRequests: ULong): Update

    fun broadcast(transaction: Transaction): Unit
}

class EsploraClient(url: String) : BlockchainClient {
    private val client = BdkEsploraClient(url)

    override fun fullScan(wallet: BdkWallet, stopGap: ULong, parallelRequests: ULong): Update {
        return client.fullScan(wallet, stopGap, parallelRequests)
    }

    override fun broadcast(transaction: Transaction) {
        client.broadcast(transaction)
    }
}

enum class ClientRank {
    DEFAULT,
    ALTERNATIVE
}
