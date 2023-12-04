package com.goldenraven.devkitwallet.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldenraven.devkitwallet.data.Wallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class WalletViewModel : ViewModel() {

    private var _balance: MutableLiveData<ULong> = MutableLiveData(0u)
    val balance: LiveData<ULong>
        get() = _balance

    private var _syncing: MutableLiveData<Boolean> = MutableLiveData(false)
    val syncing: LiveData<Boolean>
        get() = _syncing

    fun updateBalance() {
        _syncing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            Wallet.sync()
            withContext(Dispatchers.Main) {
                _balance.value = Wallet.getBalance()
                _syncing.value = false
            }
        }
    }
}
