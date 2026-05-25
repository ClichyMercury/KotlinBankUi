package com.finsim.data.network.dto

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CandleResponse(
    @Serializable(with = InstantSerializer::class) val timestamp: Instant,
    @Serializable(with = BigDecimalSerializer::class) val open: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val high: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val low: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val close: BigDecimal
)
