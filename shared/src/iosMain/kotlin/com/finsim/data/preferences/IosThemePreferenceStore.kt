package com.finsim.data.preferences

import com.finsim.ui.theme.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

class IosThemePreferenceStore : ThemePreferenceStore {

    private val defaults = NSUserDefaults.standardUserDefaults
    private val _preference = MutableStateFlow(read())

    override val preference: Flow<ThemePreference> = _preference.asStateFlow()

    override suspend fun set(preference: ThemePreference) {
        defaults.setObject(preference.name, forKey = KEY)
        _preference.value = preference
    }

    private fun read(): ThemePreference = when (defaults.stringForKey(KEY)) {
        ThemePreference.Light.name -> ThemePreference.Light
        ThemePreference.Dark.name -> ThemePreference.Dark
        else -> ThemePreference.System
    }

    private companion object {
        const val KEY = "finsim_theme_preference"
    }
}
