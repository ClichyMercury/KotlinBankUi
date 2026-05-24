package com.example.kotlinbankui.data.portfolio

import com.example.kotlinbankui.data.auth.SessionManager
import com.example.kotlinbankui.data.auth.TokenStore
import com.example.kotlinbankui.data.auth.guard
import com.example.kotlinbankui.data.network.bodyOrThrow
import com.example.kotlinbankui.data.network.dto.PortfolioResponse
import com.example.kotlinbankui.data.util.requireAuth
import com.example.kotlinbankui.data.util.runCatchingApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioRepository @Inject constructor(
    private val client: HttpClient,
    private val tokenStore: TokenStore,
    private val sessionManager: SessionManager
) {

    suspend fun getPortfolio(): Result<PortfolioResponse> = runCatchingApi {
        sessionManager.guard {
            val resp = client.get("/api/v1/portfolio") {
                requireAuth(tokenStore)
            }
            resp.bodyOrThrow<PortfolioResponse>()
        }
    }
}
