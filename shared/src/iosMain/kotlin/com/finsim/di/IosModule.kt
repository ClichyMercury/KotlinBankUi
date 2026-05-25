package com.finsim.di

import com.finsim.data.auth.IosTokenStore
import com.finsim.data.auth.TokenStore
import com.finsim.data.preferences.IosThemePreferenceStore
import com.finsim.data.preferences.ThemePreferenceStore
import org.koin.core.module.Module
import org.koin.dsl.module

val iosPlatformModule: Module = module {
    single<TokenStore> { IosTokenStore() }
    single<ThemePreferenceStore> { IosThemePreferenceStore() }
}
