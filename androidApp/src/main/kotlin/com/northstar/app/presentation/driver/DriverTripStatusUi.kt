package com.northstar.app.presentation.driver

import com.northstar.app.presentation.fleet.CarStatusLabels

object DriverTripStatusUi {
    private val questions = mapOf(
        "2" to "Вы доехали до места погрузки?",
        "3" to "Вы загрузились?",
        "4" to "Вы в пути?",
        "5" to "Вы доехали до выгрузки?",
        "6" to "Вы выгрузились?",
        "7" to "Вы готовы завершить рейс?",
        "8" to "Вы готовы ехать дальше?",
    )

    private val nextHints = mapOf(
        "2" to "Прибыла на погрузку",
        "3" to "Погружена",
        "4" to "Транзит",
        "5" to "Прибыла на выгрузку",
        "6" to "Выгружена",
        "7" to "Завершение рейса",
        "8" to "Свободна",
    )

    fun question(status: String?): String = questions[status] ?: "Нет активного шага"

    fun nextHint(status: String?): String {
        val hint = nextHints[status] ?: return ""
        return "Следующий пункт: $hint"
    }

    fun statusLabel(status: String?): String = CarStatusLabels.label(status)

    fun nextStatusCode(current: String?): String? = when (current) {
        "2" -> "3"
        "3" -> "4"
        "4" -> "5"
        "5" -> "6"
        "6" -> "7"
        "7" -> "8"
        "8" -> "1"
        else -> null
    }

    fun progressIndex(status: String?): Int = when (status) {
        "2" -> 1
        "3" -> 2
        "4" -> 3
        "5" -> 4
        "6" -> 5
        "7" -> 6
        "8" -> 7
        else -> 0
    }
}
