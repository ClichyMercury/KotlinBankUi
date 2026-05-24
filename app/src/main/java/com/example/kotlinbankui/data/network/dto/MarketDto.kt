package com.example.kotlinbankui.data.network.dto

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Serializable
enum class AssetType { STOCK, FOREX, CRYPTO }

@Serializable
data class AssetResponse(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val ticker: String,
    val name: String,
    val type: AssetType,
    val market: String,
    @Serializable(with = BigDecimalSerializer::class) val lastPrice: BigDecimal? = null,
    @Serializable(with = InstantSerializer::class) val lastPriceUpdatedAt: Instant? = null
)
