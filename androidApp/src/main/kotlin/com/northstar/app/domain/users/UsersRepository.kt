package com.northstar.app.domain.users

import com.northstar.app.data.api.UserDto

interface UsersRepository {
    suspend fun listUsers(): Result<List<UserDto>>
    suspend fun updateUserRole(userId: Int, status: String, carId: Int?): Result<UserDto>
}
