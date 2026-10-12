package com.northstar.app.data.applications

import com.northstar.app.data.api.ApplicationDto
import com.northstar.app.data.api.ApplicationsApi
import com.northstar.app.data.api.CreateApplicationRequestDto
import com.northstar.app.domain.applications.ApplicationsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerApplicationsRepository @Inject constructor(
    private val applicationsApi: ApplicationsApi,
) : ApplicationsRepository {

    override suspend fun listApplications(): Result<List<ApplicationDto>> = runCatching {
        applicationsApi.listApplications().applications
    }

    override suspend fun createApplication(
        coordStart: String,
        coordEnd: String,
        dateOfStart: String,
        weight: String,
        carNow: String,
    ): Result<ApplicationDto> = runCatching {
        applicationsApi.createApplication(
            CreateApplicationRequestDto(
                coordStart = coordStart,
                coordEnd = coordEnd,
                dateOfStart = dateOfStart,
                weight = weight,
                carNow = carNow,
            ),
        )
    }
}
