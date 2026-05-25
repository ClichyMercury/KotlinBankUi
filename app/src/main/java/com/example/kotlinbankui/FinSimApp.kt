package com.example.kotlinbankui

import android.app.Application
import com.finsim.di.ApiClientConfig
import com.finsim.di.androidPlatformModule
import com.finsim.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class FinSimApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FinSimApp)
            modules(
                module {
                    single { ApiClientConfig(baseUrl = BuildConfig.API_BASE_URL, debug = BuildConfig.DEBUG) }
                },
                androidPlatformModule,
                sharedModule,
            )
        }
    }
}
