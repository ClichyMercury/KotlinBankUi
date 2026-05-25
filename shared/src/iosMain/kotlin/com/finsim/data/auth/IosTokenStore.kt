package com.finsim.data.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

/**
 * iOS impl backed by NSUserDefaults. Adequate for an MVP — for production-grade
 * secret storage, swap to Keychain (security/Keychain Services).
 */
class IosTokenStore : TokenStore {

    private val defaults = NSUserDefaults.standardUserDefaults
    private val _token = MutableStateFlow(defaults.stringForKey(KEY))

    override val token: Flow<String?> = _token.asStateFlow()

    override suspend fun get(): String? = defaults.stringForKey(KEY)

    override suspend fun save(token: String) {
        defaults.setObject(token, forKey = KEY)
        _token.value = token
    }

    override suspend fun clear() {
        defaults.removeObjectForKey(KEY)
        _token.value = null
    }

    private companion object {
        const val KEY = "finsim_access_token"
    }
}
