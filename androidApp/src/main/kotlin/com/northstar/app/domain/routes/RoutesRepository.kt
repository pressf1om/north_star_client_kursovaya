package com.northstar.app.domain.routes

import com.northstar.app.data.api.RouteDto

interface RoutesRepository {
    suspend fun listRoutes(): Result<List<RouteDto>>

    suspend fun createRoute(
        cityStart: String,
        cityEnd: String,
        platonKm: Int?,
        autodorKm: Int?,
        ollKm: Int?,
        citiesOnRoute: String,
    ): Result<RouteDto>
}
