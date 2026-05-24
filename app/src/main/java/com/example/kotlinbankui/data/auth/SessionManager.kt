package com.example.kotlinbankui.data.auth

import com.example.kotlinbankui.data.network.ApiException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralizes session lifecycle: clears the persisted token on auth failure
 * and broadcasts a one-shot logout event for the navigation layer.
 */
@Singleton
class SessionManager @Inject constructor(
    private val tokenStore: TokenStore
) {

    private val _loggedOut = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val loggedOut: SharedFlow<Unit> = _loggedOut.asSharedFlow()

    suspend fun invalidate() {
        tokenStore.clear()
        _loggedOut.tryEmit(Unit)
    }
}

/** Run an authenticated call; on 401 wipe the session before re-throwing. */
internal suspend fun <T> SessionManager.guard(block: suspend () -> T): T = try {
    block()
} catch (e: ApiException.Unauthorized) {
    invalidate()
    throw e
}
