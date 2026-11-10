package com.northstar.app.domain.drivermessages

import com.northstar.app.data.api.DriverMessageDto

interface DriverMessagesRepository {
    suspend fun sendMessage(message: String, carNumber: String): Result<DriverMessageDto>

    suspend fun listMessages(carNumber: String? = null): Result<List<DriverMessageDto>>

    suspend fun reply(messageId: Int, reply: String): Result<DriverMessageDto>
}
