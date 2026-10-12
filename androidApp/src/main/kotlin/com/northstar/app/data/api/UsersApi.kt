package com.northstar.app.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface UsersApi {
    @GET("api/users")
    suspend fun listUsers(): UsersListResponseDto

    @PATCH("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body body: UpdateUserRoleRequestDto,
    ): UserDto
}

@Serializable
data class UpdateUserRoleRequestDto(
    val status: String,
    val carId: Int? = null,
)

@Serializable
data class UsersListResponseDto(
    val users: List<UserDto>,
)
