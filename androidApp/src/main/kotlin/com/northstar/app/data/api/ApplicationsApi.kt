package com.northstar.app.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApplicationsApi {
    @GET("api/applications")
    suspend fun listApplications(): ApplicationsListResponseDto

    @POST("api/applications")
    suspend fun createApplication(@Body body: CreateApplicationRequestDto): ApplicationDto
}

@Serializable
data class ApplicationsListResponseDto(
    val applications: List<ApplicationDto>,
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
