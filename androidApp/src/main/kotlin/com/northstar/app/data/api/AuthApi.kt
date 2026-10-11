package com.northstar.app.data.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @POST("api/auth/logout")
    suspend fun logout()

    @GET("api/auth/me")
    suspend fun me(): MeResponseDto
}

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String,
)

@Serializable
data class RegisterRequestDto(
    val username: String,
    val email: String,
    val password: String,
)

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val username: String,
    val status: String,
    val carNumber: String? = null,
    val carModel: String? = null,
)

@Serializable
data class AuthResponseDto(
    val token: String,
    val user: UserDto,
)

@Serializable
data class MeResponseDto(
    val user: UserDto,
    val authenticated: Boolean = true,
)
