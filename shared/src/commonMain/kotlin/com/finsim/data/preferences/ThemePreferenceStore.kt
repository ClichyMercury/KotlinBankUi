package com.finsim.data.preferences

import com.finsim.ui.theme.ThemePreference
import kotlinx.coroutines.flow.Flow

interface ThemePreferenceStore {
    val preference: Flow<ThemePreference>
    suspend fun set(preference: ThemePreference)
}
