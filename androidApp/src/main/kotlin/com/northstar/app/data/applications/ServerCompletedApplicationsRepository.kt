package com.northstar.app.data.applications

import com.northstar.app.data.api.ApplicationDto
import com.northstar.app.data.api.CompletedApplicationsApi
import com.northstar.app.domain.applications.CompletedApplicationsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerCompletedApplicationsRepository @Inject constructor(
    private val completedApplicationsApi: CompletedApplicationsApi,
) : CompletedApplicationsRepository {

    override suspend fun listAll(): Result<List<ApplicationDto>> = runCatching {
        completedApplicationsApi.listAll().applications
    }

    override suspend fun listByCar(carNumber: String): Result<List<ApplicationDto>> = runCatching {
        completedApplicationsApi.listByCar(carNumber.trim()).applications
    }
}
