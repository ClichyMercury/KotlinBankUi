package com.finsim.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.finsim.di.ApiClientConfig
import com.finsim.di.iosPlatformModule
import com.finsim.di.sharedModule
import com.finsim.presentation.FinSimAppRoot
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.UIKit.UIViewController

/**
 * Entry point called from iOSApp.swift via ComposeView (UIViewControllerRepresentable).
 *
 * Starts Koin on first call. baseUrl comes from the APIBaseURL Info.plist key, itself fed
 * by the API_BASE_URL build setting (Debug = dev, Release = prod).
 */
fun MainViewController(baseUrl: String, debug: Boolean): UIViewController {
    initKoinIfNeeded(baseUrl = baseUrl, debug = debug)
    return ComposeUIViewController { FinSimAppRoot() }
}

private var koinStarted = false

private fun initKoinIfNeeded(baseUrl: String, debug: Boolean) {
    if (koinStarted) return
    startKoin {
        modules(
            module {
                single { ApiClientConfig(baseUrl = baseUrl, debug = debug) }
            },
            iosPlatformModule,
            sharedModule,
        )
    }
    koinStarted = true
}
