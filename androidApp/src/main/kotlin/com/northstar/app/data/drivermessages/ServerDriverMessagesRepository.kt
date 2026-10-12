package com.northstar.app.data.drivermessages

import com.northstar.app.data.api.CreateDriverMessageRequestDto
import com.northstar.app.data.api.DriverMessageDto
import com.northstar.app.data.api.DriverMessagesApi
import com.northstar.app.domain.drivermessages.DriverMessagesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerDriverMessagesRepository @Inject constructor(
    private val driverMessagesApi: DriverMessagesApi,
) : DriverMessagesRepository {

    override suspend fun sendMessage(message: String, carNumber: String): Result<DriverMessageDto> =
        runCatching {
            driverMessagesApi.createMessage(
                CreateDriverMessageRequestDto(
                    message = message,
                    carNumber = carNumber.trim(),
                ),
            )
        }
}
