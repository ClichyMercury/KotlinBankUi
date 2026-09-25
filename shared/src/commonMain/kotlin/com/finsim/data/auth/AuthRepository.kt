package com.finsim.data.auth

import com.finsim.data.network.bodyOrThrow
import com.finsim.data.network.dto.AuthResponse
import com.finsim.data.network.dto.LoginRequest
import com.finsim.data.network.dto.LogoutRequest
import com.finsim.data.network.dto.MessageResponse
import com.finsim.data.network.dto.RegisterRequest
import com.finsim.data.network.dto.UserResponse
import com.finsim.data.util.requireAuth
import com.finsim.data.util.runCatchingApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val client: HttpClient,
    private val tokenStore: TokenStore,
    private val sessionManager: SessionManager
) {

    val token: Flow<String?> = tokenStore.token

    suspend fun register(email: String, pseudo: String, password: String): Result<AuthResponse> =
        runCatchingApi {
            val resp = client.post("/api/v1/auth/register") {
                setBody(RegisterRequest(email = email, pseudo = pseudo, password = password))
            }
            val body: AuthResponse = resp.bodyOrThrow()
            tokenStore.save(accessToken = body.accessToken, refreshToken = body.refreshToken)
            body
        }

    suspend fun login(email: String, password: String): Result<AuthResponse> =
        runCatchingApi {
            val resp = client.post("/api/v1/auth/login") {
                setBody(LoginRequest(email = email, password = password))
            }
            val body: AuthResponse = resp.bodyOrThrow()
            tokenStore.save(accessToken = body.accessToken, refreshToken = body.refreshToken)
            body
        }

    suspend fun me(): Result<UserResponse> = runCatchingApi {
        sessionManager.guard {
            val resp = client.get("/api/v1/auth/me") {
                requireAuth(tokenStore)
            }
            resp.bodyOrThrow<UserResponse>()
        }
    }

    suspend fun logout() {
        val refreshToken = tokenStore.refreshToken()
        if (refreshToken != null) {
            runCatching {
                client.post("/api/v1/auth/logout") {
                    setBody(LogoutRequest(refreshToken = refreshToken))
                }
            }
        }
        tokenStore.clear()
    }

    suspend fun logoutAll() {
        runCatching {
            sessionManager.guard {
                val resp = client.post("/api/v1/auth/logout-all") {
                    requireAuth(tokenStore)
                }
                resp.bodyOrThrow<MessageResponse>()
            }
        }
        tokenStore.clear()
    }
}
