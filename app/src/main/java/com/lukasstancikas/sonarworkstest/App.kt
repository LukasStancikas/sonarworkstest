package com.lukasstancikas.sonarworkstest

import android.app.Application
import com.lukasstancikas.sonarworkstest.di.MainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        // start Timber
        Timber.plant(Timber.DebugTree())

        // start Koin
        startKoin {
            // declare used Android context
            androidContext(this@App)
            // declare modules
            modules(MainModule.get())
        }
    }
}