package com.northstar.app.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CarsApi {
    @GET("api/cars")
    suspend fun listCars(): CarsListResponseDto

    @POST("api/cars")
    suspend fun createCar(@Body body: CreateCarRequestDto): CarDto
}

@Serializable
data class CreateCarRequestDto(
    val carNumber: String,
    val model: String,
)

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
