/*
 * Copyright 2020-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.bitcoindevkit.devkitwallet.domain.Wallet

internal class AddressViewModel : ViewModel() {
    private var _address: MutableLiveData<String?> = MutableLiveData(null)
    val address: LiveData<String?>
        get() = _address

    private var _addressIndex: MutableLiveData<UInt> = MutableLiveData(0u)
    val addressIndex: LiveData<UInt>
        get() = _addressIndex

    fun updateAddress() {
        val newAddress = Wallet.getNewAddress()
        _address.value = newAddress.address.asString()
        _addressIndex.value = newAddress.index
    }
}
