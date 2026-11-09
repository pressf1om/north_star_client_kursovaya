package com.northstar.app.presentation.fleet

data class MapCity(
    val label: String,
    val latitude: Double,
    val longitude: Double,
)

data class TransitRoutePins(
    val carNumber: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
)

object NorthStarMapCities {
    val all = listOf(
        MapCity("Москва", 55.7558, 37.6173),
        MapCity("Санкт-Петербург", 59.9343, 30.3351),
        MapCity("Екатеринбург", 56.838011, 60.597465),
        MapCity("Самара", 53.195538, 50.101783),
        MapCity("Казань", 55.795793, 49.106585),
        MapCity("Новосибирск", 55.030199, 82.920430),
    )
}

object GeoMap {
    fun parseCoordinatePair(raw: String): Pair<Double, Double>? {
        val parts = raw.split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        if (parts.size != 2) return null
        val lat = parts[0].toDoubleOrNull() ?: return null
        val lon = parts[1].toDoubleOrNull() ?: return null
        return lat to lon
    }
}
