package com.northstar.app.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DriverMessagesApi {
    @POST("api/driver-messages")
    suspend fun createMessage(@Body body: CreateDriverMessageRequestDto): DriverMessageDto

    @GET("api/driver-messages")
    suspend fun listMessages(@Query("car_number") carNumber: String? = null): DriverMessagesListResponseDto
}

@Serializable
data class CreateDriverMessageRequestDto(
    val message: String,
    val carNumber: String,
)

@Serializable
data class DriverMessageDto(
    val id: Int,
    val message: String,
    val carNumber: String,
)

@Serializable
data class DriverMessagesListResponseDto(
    val messages: List<DriverMessageDto>,
)
