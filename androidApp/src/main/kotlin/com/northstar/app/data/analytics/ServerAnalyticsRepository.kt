package com.northstar.app.data.analytics

import com.northstar.app.data.api.AnalyticsApi
import com.northstar.app.data.api.AnalyticsVariantDto
import com.northstar.app.data.api.CreateAnalyticsVariantRequestDto
import com.northstar.app.data.api.RoutesApi
import com.northstar.app.domain.analytics.AnalyticsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerAnalyticsRepository @Inject constructor(
    private val analyticsApi: AnalyticsApi,
    private val routesApi: RoutesApi,
) : AnalyticsRepository {

    override suspend fun listVariants(): Result<List<AnalyticsVariantDto>> = runCatching {
        analyticsApi.listVariants().items
    }

    override suspend fun addVariant(
        numberOfRoad: Int,
        dateStart: String,
        dateEnd: String,
        routeId: Int,
        cost: Int,
        fuelCost: Int,
    ): Result<AnalyticsVariantDto> = runCatching {
        analyticsApi.addVariant(
            CreateAnalyticsVariantRequestDto(
                numberOfRoad = numberOfRoad,
                dateStart = dateStart,
                dateEnd = dateEnd,
                routeId = routeId,
                cost = cost,
                fuelCost = fuelCost,
            ),
        )
    }

    override suspend fun clearVariants(): Result<Unit> = runCatching {
        analyticsApi.clearVariants()
    }

    override suspend fun listRoutesForPicker(): Result<List<com.northstar.app.data.api.RouteDto>> =
        runCatching {
            routesApi.listRoutes().routes
        }
}
