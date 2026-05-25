package com.finsim.data.network

import com.finsim.data.network.dto.ErrorResponse

sealed class ApiException(message: String) : RuntimeException(message) {

    class Network(cause: Throwable) : ApiException(cause.message ?: "Network error")

    class Http(
        val statusCode: Int,
        val errorCode: String,
        val userMessage: String
    ) : ApiException("$statusCode $errorCode: $userMessage") {

        companion object {
            fun from(statusCode: Int, body: ErrorResponse?): Http = Http(
                statusCode = statusCode,
                errorCode = body?.error ?: "http_$statusCode",
                userMessage = body?.message ?: "HTTP $statusCode"
            )
        }
    }

    class Unauthorized(message: String = "Session expired") : ApiException(message)

    class Unknown(cause: Throwable) : ApiException(cause.message ?: "Unknown error")
}
