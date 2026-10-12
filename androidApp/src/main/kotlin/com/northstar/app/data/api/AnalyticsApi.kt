package com.northstar.app.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface AnalyticsApi {
    @GET("api/analytics")
    suspend fun listVariants(): AnalyticsListResponseDto

    @POST("api/analytics")
    suspend fun addVariant(@Body body: CreateAnalyticsVariantRequestDto): AnalyticsVariantDto

    @DELETE("api/analytics")
    suspend fun clearVariants()
}

@Serializable
data class AnalyticsListResponseDto(
    val items: List<AnalyticsVariantDto>,
)

@Serializable
data class AnalyticsVariantDto(
    val id: Int,
    val numberOfRoad: Int? = null,
    val dateStart: String? = null,
    val dateEnd: String? = null,
    val routeId: Int? = null,
    val routeLabel: String? = null,
    val cost: Int? = null,
    val fuelCost: Int? = null,
    val finalCost: Double? = null,
)

@Serializable
data class CreateAnalyticsVariantRequestDto(
    val numberOfRoad: Int,
    val dateStart: String,
    val dateEnd: String,
    val routeId: Int,
    val cost: Int,
    val fuelCost: Int,
)
