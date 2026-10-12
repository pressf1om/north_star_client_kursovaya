package com.northstar.app.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface CarsApi {
    @GET("api/cars")
    suspend fun listCars(): CarsListResponseDto
}

@Serializable
data class CarDto(
    val id: Int,
    val carNumber: String,
    val model: String,
    val status: String? = null,
)

@Serializable
data class CarsListResponseDto(
    val cars: List<CarDto>,
)
