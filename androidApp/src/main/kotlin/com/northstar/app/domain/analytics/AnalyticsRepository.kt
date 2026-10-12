package com.northstar.app.domain.analytics

import com.northstar.app.data.api.AnalyticsVariantDto
import com.northstar.app.data.api.RouteDto

interface AnalyticsRepository {
    suspend fun listVariants(): Result<List<AnalyticsVariantDto>>
    suspend fun addVariant(
        numberOfRoad: Int,
        dateStart: String,
        dateEnd: String,
        routeId: Int,
        cost: Int,
        fuelCost: Int,
    ): Result<AnalyticsVariantDto>

    suspend fun clearVariants(): Result<Unit>
    suspend fun listRoutesForPicker(): Result<List<RouteDto>>
}
