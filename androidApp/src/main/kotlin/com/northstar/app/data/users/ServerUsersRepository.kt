package com.northstar.app.data.users

import com.northstar.app.data.api.UpdateUserRoleRequestDto
import com.northstar.app.data.api.UserDto
import com.northstar.app.data.api.UsersApi
import com.northstar.app.domain.users.UsersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerUsersRepository @Inject constructor(
    private val usersApi: UsersApi,
) : UsersRepository {

    override suspend fun listUsers(): Result<List<UserDto>> = runCatching {
        usersApi.listUsers().users
    }

    override suspend fun updateUserRole(
        userId: Int,
        status: String,
        carId: Int?,
    ): Result<UserDto> = runCatching {
        usersApi.updateUser(userId, UpdateUserRoleRequestDto(status = status, carId = carId))
    }
}
