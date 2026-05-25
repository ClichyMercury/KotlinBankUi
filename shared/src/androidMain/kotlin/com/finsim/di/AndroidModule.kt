package com.finsim.di

import com.finsim.data.auth.AndroidTokenStore
import com.finsim.data.auth.TokenStore
import com.finsim.data.preferences.AndroidThemePreferenceStore
import com.finsim.data.preferences.ThemePreferenceStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val androidPlatformModule: Module = module {
    single<TokenStore> { AndroidTokenStore(androidContext()) }
    single<ThemePreferenceStore> { AndroidThemePreferenceStore(androidContext()) }
}
