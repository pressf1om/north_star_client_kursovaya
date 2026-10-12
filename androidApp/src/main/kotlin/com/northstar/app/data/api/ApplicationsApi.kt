package com.northstar.app.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApplicationsApi {
    @GET("api/applications")
    suspend fun listApplications(): ApplicationsListResponseDto

    @GET("api/applications/by-car/{carNumber}")
    suspend fun getByCar(@Path("carNumber") carNumber: String): ApplicationDto

    @POST("api/applications")
    suspend fun createApplication(@Body body: CreateApplicationRequestDto): ApplicationDto

    @PATCH("api/applications/{id}/status")
    suspend fun updateApplicationStatus(
        @Path("id") id: Int,
        @Body body: UpdateApplicationStatusRequestDto,
    ): UpdateApplicationStatusResponseDto

    @DELETE("api/applications/{id}")
    suspend fun deleteApplication(@Path("id") id: Int)
}

@Serializable
data class ApplicationsListResponseDto(
    val applications: List<ApplicationDto>,
)

@Serializable
data class UpdateApplicationStatusRequestDto(
    val status: String,
)

@Serializable
data class UpdateApplicationStatusResponseDto(
    val archived: Boolean,
    val application: ApplicationDto? = null,
)

@Serializable
data class CreateApplicationRequestDto(
    val coordStart: String,
    val coordEnd: String,
    val dateOfStart: String,
    val weight: String,
    val carNow: String,
)

@Serializable
data class ApplicationDto(
    val id: Int,
    val coordStart: String,
    val coordEnd: String,
    val dateOfStart: String,
    val dateOfEnd: String? = null,
    val status: String? = null,
    val weight: String? = null,
    val carNow: String? = null,
)
