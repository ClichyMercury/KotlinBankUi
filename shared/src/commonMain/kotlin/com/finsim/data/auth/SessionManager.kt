package com.finsim.data.auth

import com.finsim.data.network.ApiClient
import com.finsim.data.network.ApiException
import com.finsim.data.network.dto.ErrorResponse
import com.finsim.data.network.dto.RefreshRequest
import com.finsim.data.network.dto.RefreshResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlin.concurrent.Volatile
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Centralizes session lifecycle: rotates the token pair on 401, clears the persisted tokens
 * when the refresh fails, and broadcasts a one-shot logout event for the navigation layer.
 *
 * Refresh tokens are single-use — replaying a consumed one makes the server revoke every
 * session of the user — hence the mutex: only one refresh may ever be in flight.
 */
class SessionManager(
    private val client: HttpClient,
    private val tokenStore: TokenStore
) {

    private val refreshMutex = Mutex()

    private val _loggedOut = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val loggedOut: SharedFlow<Unit> = _loggedOut.asSharedFlow()

    private var pendingMessage: String? = null

    @Volatile
    internal var generation: Int = 0
        private set

    fun consumePendingMessage(): String? = pendingMessage.also { pendingMessage = null }

    suspend fun invalidate(message: String? = null) {
        pendingMessage = message
        generation++
        tokenStore.clear()
        _loggedOut.tryEmit(Unit)
    }

    /** Exchanges the stored refresh token for a fresh pair, at most once at a time. */
    internal suspend fun refresh(seenGeneration: Int): RefreshOutcome = refreshMutex.withLock {
        if (generation != seenGeneration) return@withLock RefreshOutcome.Refreshed

        val refreshToken = tokenStore.refreshToken()
            ?: return@withLock RefreshOutcome.Failed(null)

        val response = try {
            client.post("/api/v1/auth/refresh") {
                setBody(RefreshRequest(refreshToken = refreshToken))
            }
        } catch (e: Throwable) {
            return@withLock RefreshOutcome.Unreachable
        }

        if (response.status == HttpStatusCode.Unauthorized) {
            val error = runCatching {
                ApiClient.json.decodeFromString(ErrorResponse.serializer(), response.bodyAsText())
            }.getOrNull()
            return@withLock RefreshOutcome.Failed(sessionEndedMessage(error?.message))
        }
        if (!response.status.isSuccess()) return@withLock RefreshOutcome.Failed(null)

        val body = runCatching { response.body<RefreshResponse>() }.getOrNull()
            ?: return@withLock RefreshOutcome.Failed(null)

        tokenStore.save(accessToken = body.accessToken, refreshToken = body.refreshToken)
        generation++
        RefreshOutcome.Refreshed
    }
}

internal sealed interface RefreshOutcome {
    data object Refreshed : RefreshOutcome
    data class Failed(val message: String?) : RefreshOutcome
    data object Unreachable : RefreshOutcome
}

private fun sessionEndedMessage(serverMessage: String?): String? = when {
    serverMessage == null -> null
    serverMessage.contains("reuse", ignoreCase = true) ->
        "Session révoquée pour raison de sécurité. Reconnecte-toi."
    else -> null
}

/** Run an authenticated call; on 401 rotate the token pair and replay the call once. */
internal suspend fun <T> SessionManager.guard(block: suspend () -> T): T {
    val generationBeforeCall = generation
    return try {
        block()
    } catch (e: ApiException.Unauthorized) {
        when (val outcome = refresh(generationBeforeCall)) {
            is RefreshOutcome.Refreshed -> try {
                block()
            } catch (retried: ApiException.Unauthorized) {
                invalidate()
                throw retried
            }

            is RefreshOutcome.Failed -> {
                invalidate(outcome.message)
                throw e
            }

            is RefreshOutcome.Unreachable -> throw e
        }
    }
}
