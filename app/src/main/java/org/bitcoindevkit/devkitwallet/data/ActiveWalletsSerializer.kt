/*
 * Copyright 2021-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object ActiveWalletsSerializer : Serializer<ActiveWallets> {
    override val defaultValue: ActiveWallets = ActiveWallets.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ActiveWallets {
        try {
            return ActiveWallets.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ActiveWallets, output: OutputStream) {
        t.writeTo(output)
    }
}

object SingleWalletSerializer : Serializer<SingleWallet> {
    override val defaultValue: SingleWallet = SingleWallet.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SingleWallet {
        try {
            return SingleWallet.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: SingleWallet, output: OutputStream) {
        t.writeTo(output)
    }
}
