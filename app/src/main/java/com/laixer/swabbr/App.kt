package com.laixer.swabbr

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.laixer.cache.CacheLibrary
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Unique initialization of Cache library to allow saving into device
        CacheLibrary.init(this)
        // Unique initialization of Dependency Injection library to allow the use of application context
        startKoin { androidContext(this@App) }
        // Determine if user is in night mode
        val nightMode = when (resources.configuration.uiMode) {
            Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}
