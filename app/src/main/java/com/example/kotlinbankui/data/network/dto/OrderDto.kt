package com.example.kotlinbankui.data.network.dto

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Serializable
enum class OrderType { BUY, SELL }

@Serializable
enum class OrderStatus { PENDING, EXECUTED, FAILED }

@Serializable
data class BuyOrderRequest(
    @Serializable(with = UUIDSerializer::class) val assetId: UUID,
    @Serializable(with = BigDecimalSerializer::class) val quantity: BigDecimal
)

@Serializable
data class OrderResponse(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @Serializable(with = UUIDSerializer::class) val assetId: UUID,
    val type: OrderType,
    @Serializable(with = BigDecimalSerializer::class) val quantity: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val price: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val total: BigDecimal,
    val status: OrderStatus,
    @Serializable(with = InstantSerializer::class) val createdAt: Instant,
    @Serializable(with = InstantSerializer::class) val executedAt: Instant? = null
)
