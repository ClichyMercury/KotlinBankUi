package com.finsim.data.orders

import com.finsim.data.auth.SessionManager
import com.finsim.data.auth.TokenStore
import com.finsim.data.auth.guard
import com.finsim.data.network.bodyOrThrow
import com.finsim.data.network.dto.BuyOrderRequest
import com.finsim.data.network.dto.OrderResponse
import com.finsim.data.util.requireAuth
import com.finsim.data.util.runCatchingApi
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlin.uuid.Uuid

class OrderRepository(
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

    suspend fun placeBuy(assetId: Uuid, quantity: BigDecimal): Result<OrderResponse> = runCatchingApi {
        sessionManager.guard {
            val resp = client.post("/api/v1/orders/buy") {
                requireAuth(tokenStore)
                setBody(BuyOrderRequest(assetId = assetId, quantity = quantity))
            }
            resp.bodyOrThrow<OrderResponse>()
        }
    }
}
