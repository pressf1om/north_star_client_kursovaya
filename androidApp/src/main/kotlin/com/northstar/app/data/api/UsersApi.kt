package com.northstar.app.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface UsersApi {
    @GET("api/users")
    suspend fun listUsers(): UsersListResponseDto
}

@Serializable
data class UsersListResponseDto(
    val users: List<UserDto>,
)
