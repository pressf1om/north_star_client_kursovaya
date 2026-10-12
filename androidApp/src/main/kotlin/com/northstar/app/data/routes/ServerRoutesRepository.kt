package com.northstar.app.data.routes

import com.northstar.app.data.api.CreateRouteRequestDto
import com.northstar.app.data.api.RouteDto
import com.northstar.app.data.api.RoutesApi
import com.northstar.app.domain.routes.RoutesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerRoutesRepository @Inject constructor(
    private val routesApi: RoutesApi,
) : RoutesRepository {

    override suspend fun listRoutes(): Result<List<RouteDto>> = runCatching {
        routesApi.listRoutes().routes
    }

    override suspend fun createRoute(
        cityStart: String,
        cityEnd: String,
        platonKm: Int?,
        autodorKm: Int?,
        ollKm: Int?,
        citiesOnRoute: String,
    ): Result<RouteDto> = runCatching {
        routesApi.createRoute(
            CreateRouteRequestDto(
                cityStart = cityStart,
                cityEnd = cityEnd,
                platonKm = platonKm,
                autodorKm = autodorKm,
                ollKm = ollKm,
                citiesOnRoute = citiesOnRoute,
            ),
        )
    }
}
