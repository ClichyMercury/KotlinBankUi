package com.finsim.data.network.dto

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.uuid.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class OrderType { BUY, SELL }

@Serializable
enum class OrderStatus { PENDING, EXECUTED, FAILED }

@Serializable
data class BuyOrderRequest(
    @Serializable(with = UuidSerializer::class) val assetId: Uuid,
    @Serializable(with = BigDecimalSerializer::class) val quantity: BigDecimal
)

@Serializable
data class OrderResponse(
    @Serializable(with = UuidSerializer::class) val id: Uuid,
    @Serializable(with = UuidSerializer::class) val assetId: Uuid,
    val type: OrderType,
    @Serializable(with = BigDecimalSerializer::class) val quantity: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val price: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val total: BigDecimal,
    val status: OrderStatus,
    @Serializable(with = InstantSerializer::class) val createdAt: Instant,
    @Serializable(with = InstantSerializer::class) val executedAt: Instant? = null
)
