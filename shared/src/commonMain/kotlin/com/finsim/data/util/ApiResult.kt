package com.finsim.data.util

import com.finsim.data.auth.TokenStore
import com.finsim.data.network.ApiException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth

/** Wrap a network call and normalize errors into [ApiException]. */
internal inline fun <T> runCatchingApi(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: ApiException) {
    Result.failure(e)
} catch (e: Throwable) {
    Result.failure(ApiException.Network(e))
}

/** Attach bearer token if available. Throws [ApiException.Unauthorized] otherwise. */
internal suspend fun HttpRequestBuilder.requireAuth(tokenStore: TokenStore) {
    val token = tokenStore.get() ?: throw ApiException.Unauthorized("No token")
    bearerAuth(token)
}
