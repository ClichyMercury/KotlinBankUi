package com.finsim.data.auth

import kotlinx.coroutines.flow.Flow

interface TokenStore {
    val token: Flow<String?>
    suspend fun get(): String?
    suspend fun save(token: String)
    suspend fun clear()
}
