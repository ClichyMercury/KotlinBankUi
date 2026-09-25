package com.finsim.data.auth

import kotlinx.coroutines.flow.Flow

interface TokenStore {
    val token: Flow<String?>
    suspend fun get(): String?
    suspend fun refreshToken(): String?
    suspend fun save(accessToken: String, refreshToken: String?)
    suspend fun clear()
}
