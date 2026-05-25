package com.finsim.data.market

import com.finsim.data.network.bodyOrThrow
import com.finsim.data.network.dto.AssetResponse
import com.finsim.data.network.dto.AssetType
import com.finsim.data.network.dto.CandleResponse
import com.finsim.data.util.runCatchingApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlin.uuid.Uuid

class MarketRepository(private val client: HttpClient) {

    suspend fun listAssets(type: AssetType? = null): Result<List<AssetResponse>> = runCatchingApi {
        val resp = client.get("/api/v1/market/assets") {
            type?.let { parameter("type", it.name) }
        }
        resp.bodyOrThrow<List<AssetResponse>>()
    }

    suspend fun getAsset(id: Uuid): Result<AssetResponse> = runCatchingApi {
        val resp = client.get("/api/v1/market/assets/$id")
        resp.bodyOrThrow<AssetResponse>()
    }

    suspend fun getCandles(id: Uuid, days: Int): Result<List<CandleResponse>> = runCatchingApi {
        val resp = client.get("/api/v1/market/assets/$id/candles") {
            parameter("days", days)
        }
        resp.bodyOrThrow<List<CandleResponse>>()
    }
}
