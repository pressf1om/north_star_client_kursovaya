package com.northstar.app.presentation.fleet

object CarStatusLabels {
    private val byCode = mapOf(
        "1" to "Свободна",
        "2" to "Назначена",
        "3" to "Прибыла на погрузку",
        "4" to "Погружена",
        "5" to "Транзит",
        "6" to "Прибыла на выгрузку",
        "7" to "Выгружена",
        "8" to "Завершение рейса",
        "9" to "На Т.О.",
    )

    fun label(code: String?): String = byCode[code] ?: "Неизвестно"

    fun isFree(code: String?): Boolean = code == "1" || code.isNullOrBlank()
}
