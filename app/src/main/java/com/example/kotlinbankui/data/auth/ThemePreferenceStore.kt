package com.example.kotlinbankui.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.finsim.ui.theme.ThemePreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.themeDataStore by preferencesDataStore(name = "finsim_theme")
private val THEME_KEY = stringPreferencesKey("preference")

@Singleton
class ThemePreferenceStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val preference: Flow<ThemePreference> = context.themeDataStore.data.map { prefs ->
        when (prefs[THEME_KEY]) {
            ThemePreference.Light.name -> ThemePreference.Light
            ThemePreference.Dark.name -> ThemePreference.Dark
            else -> ThemePreference.System
        }
    }

    suspend fun set(preference: ThemePreference) {
        context.themeDataStore.edit { it[THEME_KEY] = preference.name }
    }
}
