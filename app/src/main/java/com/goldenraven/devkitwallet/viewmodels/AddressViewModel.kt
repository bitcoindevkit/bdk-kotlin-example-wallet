package com.goldenraven.devkitwallet.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goldenraven.devkitwallet.domain.Wallet

internal class AddressViewModel : ViewModel() {
    private var _address: MutableLiveData<String> = MutableLiveData("No address yet")
    private var _addressIndex: MutableLiveData<UInt> = MutableLiveData(0u)
    val address: LiveData<String>
        get() = _address
    val addressIndex: LiveData<UInt>
        get() = _addressIndex

    fun updateAddress() {
        val newAddress = Wallet.getNewAddress()
        _address.value = newAddress.address.asString()
        _addressIndex.value = newAddress.index
    }
}
