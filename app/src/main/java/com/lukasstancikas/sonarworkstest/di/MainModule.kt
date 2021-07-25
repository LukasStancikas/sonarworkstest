package com.lukasstancikas.sonarworkstest.di

import com.lukasstancikas.sonarworkstest.bridge.WebBridge
import com.lukasstancikas.sonarworkstest.bridge.android.WebBridgeImpl
import com.lukasstancikas.sonarworkstest.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object MainModule {
    fun get(): Module {
        return module {
            single<WebBridge> { WebBridgeImpl() }
            viewModel { MainViewModel() }
        }
    }
}