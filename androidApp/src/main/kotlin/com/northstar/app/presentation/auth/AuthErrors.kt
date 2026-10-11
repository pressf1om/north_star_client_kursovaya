package com.northstar.app.presentation.auth

import com.northstar.app.R
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException

fun Throwable.toAuthMessage(fallbackRes: Int): String {
    if (this is HttpException) {
        val body = response()?.errorBody()?.string()
        if (!body.isNullOrBlank()) {
            runCatching {
                val json = Json.parseToJsonElement(body).jsonObject
                json["message"]?.jsonPrimitive?.content
            }.getOrNull()?.let { return it }
        }
        when (code()) {
            401 -> return "Неверный логин или пароль"
            409 -> return "Этот логин уже занят"
        }
    }
    return message?.takeIf { it.isNotBlank() }
        ?: "Ошибка (код ${(this as? HttpException)?.code() ?: "—"})"
}
