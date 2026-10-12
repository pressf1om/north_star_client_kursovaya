package com.northstar.app.data.cars

import com.northstar.app.data.api.CarDto
import com.northstar.app.data.api.CarsApi
import com.northstar.app.data.api.CreateCarRequestDto
import com.northstar.app.domain.cars.CarsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerCarsRepository @Inject constructor(
    private val carsApi: CarsApi,
) : CarsRepository {

    override suspend fun listCars(status: String?): Result<List<CarDto>> = runCatching {
        carsApi.listCars(status).cars
    }

    override suspend fun createCar(carNumber: String, model: String): Result<CarDto> = runCatching {
        carsApi.createCar(CreateCarRequestDto(carNumber = carNumber, model = model))
    }
}
