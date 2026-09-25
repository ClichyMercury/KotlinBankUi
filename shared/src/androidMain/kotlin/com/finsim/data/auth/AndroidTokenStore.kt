@file:Suppress("DEPRECATION")

package com.finsim.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

private const val FILE_NAME = "finsim_auth_secure"
private const val ACCESS_KEY = "access_token"
private const val REFRESH_KEY = "refresh_token"

class AndroidTokenStore(private val context: Context) : TokenStore {

    private val _token = MutableStateFlow<String?>(null)

    override val token: Flow<String?> = flow {
        _token.value = get()
        emitAll(_token)
    }

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override suspend fun get(): String? = read(ACCESS_KEY)

    override suspend fun refreshToken(): String? = read(REFRESH_KEY)

    override suspend fun save(accessToken: String, refreshToken: String?) {
        withContext(Dispatchers.IO) {
            prefs.edit()
                .putString(ACCESS_KEY, accessToken)
                .apply { if (refreshToken != null) putString(REFRESH_KEY, refreshToken) else remove(REFRESH_KEY) }
                .commit()
        }
        _token.value = accessToken
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            prefs.edit()
                .remove(ACCESS_KEY)
                .remove(REFRESH_KEY)
                .commit()
        }
        _token.value = null
    }

    private suspend fun read(key: String): String? = withContext(Dispatchers.IO) {
        prefs.getString(key, null)
    }
}
