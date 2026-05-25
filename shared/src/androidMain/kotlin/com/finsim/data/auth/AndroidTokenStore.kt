package com.finsim.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "finsim_auth")
private val TOKEN_KEY = stringPreferencesKey("access_token")

class AndroidTokenStore(private val context: Context) : TokenStore {

    override val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    override suspend fun get(): String? = token.first()

    override suspend fun save(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    override suspend fun clear() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}
