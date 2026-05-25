package com.finsim.data.network

import com.finsim.data.network.dto.ErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiClient {

    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    fun create(baseUrl: String, debug: Boolean = false): HttpClient = HttpClient {
        expectSuccess = false

        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("FinSimHttp: $message")
                }
            }
            level = if (debug) LogLevel.INFO else LogLevel.NONE
        }

        defaultRequest {
            url.takeFrom(URLBuilder(baseUrl))
            contentType(ContentType.Application.Json)
        }
    }
}

suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T {
    if (status.isSuccess()) return body()
    val error = runCatching { ApiClient.json.decodeFromString(ErrorResponse.serializer(), bodyAsText()) }.getOrNull()
    when (status.value) {
        HttpStatusCode.Unauthorized.value -> throw ApiException.Unauthorized(error?.message ?: "Unauthorized")
        else -> throw ApiException.Http.from(status.value, error)
    }
}
