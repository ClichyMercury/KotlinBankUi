package com.example.kotlinbankui.data.orders

import com.example.kotlinbankui.data.auth.SessionManager
import com.example.kotlinbankui.data.auth.TokenStore
import com.example.kotlinbankui.data.auth.guard
import com.example.kotlinbankui.data.network.bodyOrThrow
import com.example.kotlinbankui.data.network.dto.BuyOrderRequest
import com.example.kotlinbankui.data.network.dto.OrderResponse
import com.example.kotlinbankui.data.util.requireAuth
import com.example.kotlinbankui.data.util.runCatchingApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val client: HttpClient,
    private val tokenStore: TokenStore,
    private val sessionManager: SessionManager
) {

    suspend fun listOrders(): Result<List<OrderResponse>> = runCatchingApi {
        sessionManager.guard {
            val resp = client.get("/api/v1/orders") {
                requireAuth(tokenStore)
            }
            resp.bodyOrThrow<List<OrderResponse>>()
        }
    }

    suspend fun placeBuy(assetId: UUID, quantity: BigDecimal): Result<OrderResponse> = runCatchingApi {
        sessionManager.guard {
            val resp = client.post("/api/v1/orders/buy") {
                requireAuth(tokenStore)
                setBody(BuyOrderRequest(assetId = assetId, quantity = quantity))
            }
            resp.bodyOrThrow<OrderResponse>()
        }
    }
}
