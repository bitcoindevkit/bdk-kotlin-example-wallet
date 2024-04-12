/*
 * Copyright 2020-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet

import android.app.Application
// import org.bitcoindevkit.devkitwallet.utils.SharedPreferencesManager
// import org.bitcoindevkit.devkitwallet.domain.Repository
import org.bitcoindevkit.devkitwallet.domain.Wallet

class DevkitWalletApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // initialize Wallet object (singleton) with path variable
        Wallet.setPath(applicationContext.filesDir.toString())

        // initialize shared preferences manager object (singleton)
        // val sharedPreferencesManager = SharedPreferencesManager(
        //     sharedPreferences = applicationContext.getSharedPreferences("current_wallet", MODE_PRIVATE)
        // )
        // initialize Repository object with shared preferences
        // Repository.setSharedPreferences(sharedPreferencesManager)
    }
}
