package com.northstar.app.data.applications

import com.northstar.app.data.api.ApplicationDto
import com.northstar.app.data.api.ApplicationsApi
import com.northstar.app.data.api.CreateApplicationRequestDto
import com.northstar.app.data.api.UpdateApplicationStatusRequestDto
import com.northstar.app.domain.applications.ApplicationsRepository
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.HttpException

@Singleton
class ServerApplicationsRepository @Inject constructor(
    private val applicationsApi: ApplicationsApi,
) : ApplicationsRepository {

    override suspend fun listApplications(): Result<List<ApplicationDto>> = runCatching {
        applicationsApi.listApplications().applications
    }

    override suspend fun getByCarNumber(carNumber: String): Result<ApplicationDto?> = runCatching {
        try {
            applicationsApi.getByCar(carNumber.trim())
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        }
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

    override suspend fun updateApplicationStatus(
        applicationId: Int,
        status: String,
    ): Result<ApplicationDto?> = runCatching {
        val response = applicationsApi.updateApplicationStatus(
            applicationId,
            UpdateApplicationStatusRequestDto(status = status),
        )
        if (response.archived) null else response.application
    }
}
