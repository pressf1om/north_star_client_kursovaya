package com.northstar.app.domain.cars

import com.northstar.app.data.api.CarDto

interface CarsRepository {
    suspend fun listCars(): Result<List<CarDto>>
}
