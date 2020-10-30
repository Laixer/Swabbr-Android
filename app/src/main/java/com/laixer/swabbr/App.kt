package com.laixer.swabbr

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gu.toolargetool.TooLargeTool
import com.laixer.cache.CacheLibrary
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    private val crashlytics: FirebaseCrashlytics by inject()

    override fun onCreate() {
        super.onCreate()

        TooLargeTool.startLogging(this)

        // Unique initialization of Dependency Injection library to allow the use of application context
        startKoin { androidContext(this@App) }

        // Inject our dependencies straight away (for this Application instance only)
        injectFeature()

        crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        // Unique initialization of Cache library to allow saving into device
        CacheLibrary.init(this)

        // Determine if user is in night mode
        val nightMode = when (resources.configuration.uiMode) {
            Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }

        AppCompatDelegate.setDefaultNightMode(nightMode)

        // Launch screen timeout, this is not material guideline compliant but client is king and
        // most want it displayed longer, just remove if client is material compliant ^^.
        Thread.sleep(1000)
    }
}
