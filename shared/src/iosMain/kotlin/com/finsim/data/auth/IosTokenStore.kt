package com.finsim.data.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

private const val ACCESS_ACCOUNT = "access_token"
private const val REFRESH_ACCOUNT = "refresh_token"

class IosTokenStore : TokenStore {

    private val _token = MutableStateFlow<String?>(null)

    override val token: Flow<String?> = flow {
        _token.value = get()
        emitAll(_token)
    }

    override suspend fun get(): String? = Keychain.read(ACCESS_ACCOUNT)

    override suspend fun refreshToken(): String? = Keychain.read(REFRESH_ACCOUNT)

    override suspend fun save(accessToken: String, refreshToken: String?) {
        Keychain.write(ACCESS_ACCOUNT, accessToken)
        if (refreshToken != null) {
            Keychain.write(REFRESH_ACCOUNT, refreshToken)
        } else {
            Keychain.delete(REFRESH_ACCOUNT)
        }
        _token.value = accessToken
    }

    override suspend fun clear() {
        Keychain.delete(ACCESS_ACCOUNT)
        Keychain.delete(REFRESH_ACCOUNT)
        _token.value = null
    }
}
