package com.northstar.app.domain.applications

import com.northstar.app.data.api.ApplicationDto

interface ApplicationsRepository {
    suspend fun listApplications(): Result<List<ApplicationDto>>

    suspend fun getByCarNumber(carNumber: String): Result<ApplicationDto?>

    suspend fun createApplication(
        coordStart: String,
        coordEnd: String,
        dateOfStart: String,
        weight: String,
        carNow: String,
    ): Result<ApplicationDto>

    /** `null` в успешном результате — заявка ушла в архив (статус 1). */
    suspend fun updateApplicationStatus(applicationId: Int, status: String): Result<ApplicationDto?>
}
