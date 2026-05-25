package com.finsim.data.portfolio

import com.finsim.data.auth.SessionManager
import com.finsim.data.auth.TokenStore
import com.finsim.data.auth.guard
import com.finsim.data.network.bodyOrThrow
import com.finsim.data.network.dto.PortfolioResponse
import com.finsim.data.util.requireAuth
import com.finsim.data.util.runCatchingApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class PortfolioRepository(
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
