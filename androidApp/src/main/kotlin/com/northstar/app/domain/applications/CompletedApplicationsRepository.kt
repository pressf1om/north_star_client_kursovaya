package com.northstar.app.domain.applications

import com.northstar.app.data.api.ApplicationDto

interface CompletedApplicationsRepository {
    suspend fun listAll(): Result<List<ApplicationDto>>
    suspend fun listByCar(carNumber: String): Result<List<ApplicationDto>>
}
