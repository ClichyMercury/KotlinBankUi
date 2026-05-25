package com.finsim.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.finsim.ui.theme.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "finsim_theme")
private val THEME_KEY = stringPreferencesKey("preference")

class AndroidThemePreferenceStore(private val context: Context) : ThemePreferenceStore {

    override val preference: Flow<ThemePreference> = context.themeDataStore.data.map { prefs ->
        when (prefs[THEME_KEY]) {
            ThemePreference.Light.name -> ThemePreference.Light
            ThemePreference.Dark.name -> ThemePreference.Dark
            else -> ThemePreference.System
        }
    }

    override suspend fun set(preference: ThemePreference) {
        context.themeDataStore.edit { it[THEME_KEY] = preference.name }
    }
}
