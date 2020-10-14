package com.laixer.swabbr.presentation.auth

import android.accounts.AbstractAccountAuthenticator
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.laixer.swabbr.injectFeature
import org.koin.android.ext.android.inject

class AuthenticatorService : Service() {
    private val authenticatorManager: AbstractAccountAuthenticator by inject()

    override fun onCreate() {
        super.onCreate()
        injectFeature()
    }

    override fun onBind(intent: Intent): IBinder = authenticatorManager.iBinder
}
