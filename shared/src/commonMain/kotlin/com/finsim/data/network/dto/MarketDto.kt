package com.finsim.data.network.dto

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.uuid.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class AssetType { STOCK, FOREX, CRYPTO }

@Serializable
data class AssetResponse(
    @Serializable(with = UuidSerializer::class) val id: Uuid,
    val ticker: String,
    val name: String,
    val type: AssetType,
    val market: String,
    @Serializable(with = BigDecimalSerializer::class) val lastPrice: BigDecimal? = null,
    @Serializable(with = InstantSerializer::class) val lastPriceUpdatedAt: Instant? = null
)
