package com.northstar.app.domain.applications

import com.northstar.app.data.api.ApplicationDto

interface ApplicationsRepository {
    suspend fun createApplication(
        coordStart: String,
        coordEnd: String,
        dateOfStart: String,
        weight: String,
        carNow: String,
    ): Result<ApplicationDto>
}
