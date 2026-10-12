package com.northstar.app.domain.drivermessages

import com.northstar.app.data.api.DriverMessageDto

interface DriverMessagesRepository {
    suspend fun sendMessage(message: String, carNumber: String): Result<DriverMessageDto>
}
