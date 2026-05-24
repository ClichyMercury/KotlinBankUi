package com.example.kotlinbankui.data.market

import com.example.kotlinbankui.data.network.bodyOrThrow
import com.example.kotlinbankui.data.network.dto.AssetResponse
import com.example.kotlinbankui.data.network.dto.AssetType
import com.example.kotlinbankui.data.util.runCatchingApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketRepository @Inject constructor(
    private val client: HttpClient
) {

    suspend fun listAssets(type: AssetType? = null): Result<List<AssetResponse>> = runCatchingApi {
        val resp = client.get("/api/v1/market/assets") {
            type?.let { parameter("type", it.name) }
        }
        resp.bodyOrThrow<List<AssetResponse>>()
    }

    suspend fun getAsset(id: UUID): Result<AssetResponse> = runCatchingApi {
        val resp = client.get("/api/v1/market/assets/$id")
        resp.bodyOrThrow<AssetResponse>()
    }
}
