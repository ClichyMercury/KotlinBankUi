package com.example.kotlinbankui.data.auth

import com.example.kotlinbankui.data.network.ApiException
import com.example.kotlinbankui.data.network.bodyOrThrow
import com.example.kotlinbankui.data.network.dto.AuthResponse
import com.example.kotlinbankui.data.network.dto.LoginRequest
import com.example.kotlinbankui.data.network.dto.RegisterRequest
import com.example.kotlinbankui.data.network.dto.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
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
            tokenStore.save(body.accessToken)
            body
        }

    suspend fun login(email: String, password: String): Result<AuthResponse> =
        runCatchingApi {
            val resp = client.post("/api/v1/auth/login") {
                setBody(LoginRequest(email = email, password = password))
            }
            val body: AuthResponse = resp.bodyOrThrow()
            tokenStore.save(body.accessToken)
            body
        }

    suspend fun me(): Result<UserResponse> = runCatchingApi {
        val token = tokenStore.get() ?: throw ApiException.Unauthorized("No token")
        sessionManager.guard {
            val resp = client.get("/api/v1/auth/me") {
                bearerAuth(token)
            }
            resp.bodyOrThrow<UserResponse>()
        }
    }

    suspend fun logout() {
        tokenStore.clear()
    }
}

private inline fun <T> runCatchingApi(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: ApiException) {
    Result.failure(e)
} catch (e: Throwable) {
    Result.failure(ApiException.Network(e))
}
