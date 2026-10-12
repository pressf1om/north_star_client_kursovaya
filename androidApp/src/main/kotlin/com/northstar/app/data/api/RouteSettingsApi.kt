package com.northstar.app.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface RouteSettingsApi {
    @GET("api/route-settings")
    suspend fun getSettings(): RouteSettingsDto

    @PUT("api/route-settings")
    suspend fun updateSettings(@Body body: UpdateRouteSettingsRequestDto): RouteSettingsDto
}

@Serializable
data class RouteSettingsDto(
    val driverSalary: Double? = null,
    val platonCost: Double? = null,
)

@Serializable
data class UpdateRouteSettingsRequestDto(
    val driverSalary: Double,
    val platonCost: Double,
)
