package com.northstar.app.data.remote

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException

fun Throwable.apiErrorMessage(fallback: String): String {
    if (this is HttpException) {
        val body = response()?.errorBody()?.string()
        if (!body.isNullOrBlank()) {
            runCatching {
                Json.parseToJsonElement(body).jsonObject["message"]?.jsonPrimitive?.content
            }.getOrNull()?.takeIf { it.isNotBlank() }?.let { return it }
        }
    }
    return message?.takeIf { it.isNotBlank() } ?: fallback
}

fun isValidCoordinatePair(raw: String): Boolean {
    val parts = raw.split(',').map { it.trim() }
    if (parts.size != 2) return false
    return parts[0].toDoubleOrNull() != null && parts[1].toDoubleOrNull() != null
}
