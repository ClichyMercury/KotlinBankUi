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
 * Starts Koin on first call. baseUrl defaults to the iOS Simulator's loopback to host
 * (the macOS running the simulator), which is reachable via `localhost:8080`.
 */
fun MainViewController(): UIViewController {
    initKoinIfNeeded()
    return ComposeUIViewController { FinSimAppRoot() }
}

private var koinStarted = false

private fun initKoinIfNeeded() {
    if (koinStarted) return
    startKoin {
        modules(
            module {
                single { ApiClientConfig(baseUrl = "http://localhost:8080", debug = true) }
            },
            iosPlatformModule,
            sharedModule,
        )
    }
    koinStarted = true
}
