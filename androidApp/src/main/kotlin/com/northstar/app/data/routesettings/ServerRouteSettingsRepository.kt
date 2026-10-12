package com.northstar.app.data.routesettings

import com.northstar.app.data.api.RouteSettingsApi
import com.northstar.app.data.api.RouteSettingsDto
import com.northstar.app.data.api.UpdateRouteSettingsRequestDto
import com.northstar.app.domain.routesettings.RouteSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerRouteSettingsRepository @Inject constructor(
    private val routeSettingsApi: RouteSettingsApi,
) : RouteSettingsRepository {

    override suspend fun getSettings(): Result<RouteSettingsDto> = runCatching {
        routeSettingsApi.getSettings()
    }

    override suspend fun updateSettings(
        driverSalary: Double,
        platonCost: Double,
    ): Result<RouteSettingsDto> = runCatching {
        routeSettingsApi.updateSettings(
            UpdateRouteSettingsRequestDto(
                driverSalary = driverSalary,
                platonCost = platonCost,
            ),
        )
    }
}
