package com.northstar.app.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface CompletedApplicationsApi {
    @GET("api/completed_applications")
    suspend fun listAll(): ApplicationsListResponseDto

    @GET("api/completed_applications/{carNumber}")
    suspend fun listByCar(@Path("carNumber") carNumber: String): ApplicationsListResponseDto
}
