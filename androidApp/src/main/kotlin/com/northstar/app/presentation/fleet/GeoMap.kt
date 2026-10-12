package com.northstar.app.presentation.fleet

import androidx.compose.ui.geometry.Offset

data class MapCity(
    val label: String,
    val latitude: Double,
    val longitude: Double,
)

data class TransitRoutePins(
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
)

object NorthStarMapCities {
    val all = listOf(
        MapCity("Москва", 55.7558, 37.6173),
        MapCity("СПб", 59.9343, 30.3351),
        MapCity("Екб", 56.8389, 60.6057),
        MapCity("Самара", 53.1959, 50.1002),
        MapCity("Казань", 55.7963, 49.1088),
        MapCity("Нск", 55.0084, 82.9357),
    )
}

object GeoMap {
    private const val MIN_LAT = 52.0
    private const val MAX_LAT = 61.0
    private const val MIN_LON = 28.0
    private const val MAX_LON = 88.0

    fun latLonToFraction(latitude: Double, longitude: Double): Offset {
        val x = ((longitude - MIN_LON) / (MAX_LON - MIN_LON)).toFloat().coerceIn(0.05f, 0.95f)
        val y = ((MAX_LAT - latitude) / (MAX_LAT - MIN_LAT)).toFloat().coerceIn(0.05f, 0.95f)
        return Offset(x, y)
    }

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
