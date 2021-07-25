package com.lukasstancikas.sonarworkstest

import android.app.Application
import com.lukasstancikas.sonarworkstest.di.MainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        // start Koin!
        startKoin {
            // declare used Android context
            androidContext(this@App)
            // declare modules
            modules(MainModule.get())
        }
    }
}