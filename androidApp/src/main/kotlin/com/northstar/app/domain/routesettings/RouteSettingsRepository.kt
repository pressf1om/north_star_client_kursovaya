package com.northstar.app.domain.routesettings

import com.northstar.app.data.api.RouteSettingsDto

interface RouteSettingsRepository {
    suspend fun getSettings(): Result<RouteSettingsDto>

    suspend fun updateSettings(driverSalary: Double, platonCost: Double): Result<RouteSettingsDto>
}
