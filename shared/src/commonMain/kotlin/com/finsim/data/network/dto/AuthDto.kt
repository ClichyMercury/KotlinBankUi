package com.finsim.data.network.dto

import kotlin.uuid.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val pseudo: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresInSeconds: Long,
    val user: UserResponse
)

@Serializable
data class UserResponse(
    @Serializable(with = UuidSerializer::class) val id: Uuid,
    val email: String,
    val pseudo: String,
    @Serializable(with = InstantSerializer::class) val createdAt: Instant
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)
