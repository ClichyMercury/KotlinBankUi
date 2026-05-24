package com.example.kotlinbankui.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "finsim_auth")
private val TOKEN_KEY = stringPreferencesKey("access_token")

@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    suspend fun get(): String? = token.first()

    suspend fun save(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}
