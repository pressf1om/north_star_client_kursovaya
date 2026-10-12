package com.northstar.app.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RoutesApi {
    @GET("api/routes")
    suspend fun listRoutes(): RoutesListResponseDto

    @POST("api/routes")
    suspend fun createRoute(@Body body: CreateRouteRequestDto): RouteDto
}

@Serializable
data class RoutesListResponseDto(
    val routes: List<RouteDto>,
)

@Serializable
data class RouteDto(
    val id: Int,
    val cityStart: String,
    val cityEnd: String,
    val platonKm: Int? = null,
    val autodorKm: Int? = null,
    val ollKm: Int? = null,
    val citiesOnRoute: List<String> = emptyList(),
)

@Serializable
data class CreateRouteRequestDto(
    val cityStart: String,
    val cityEnd: String,
    val platonKm: Int? = null,
    val autodorKm: Int? = null,
    val ollKm: Int? = null,
    val citiesOnRoute: String = "",
)
