package com.finsim.data.network.dto

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
data class PortfolioResponse(
    @Serializable(with = BigDecimalSerializer::class) val balanceFictif: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val assetsValue: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val totalValue: BigDecimal,
    val assets: List<PortfolioAssetResponse>
)

@Serializable
data class PortfolioAssetResponse(
    @Serializable(with = UuidSerializer::class) val assetId: Uuid,
    val ticker: String,
    val name: String,
    @Serializable(with = BigDecimalSerializer::class) val quantity: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val avgBuyPrice: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val currentPrice: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class) val currentValue: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val unrealizedPnl: BigDecimal
)
