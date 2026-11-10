package com.northstar.app.presentation.fleet

import kotlin.math.abs

data class MapCity(
    val label: String,
    val latitude: Double,
    val longitude: Double,
)

data class FleetMapRoute(
    val applicationId: Int,
    val carNumber: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val startCity: String,
    val endCity: String,
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
    fun parsePoint(raw: String): Pair<Double, Double>? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        cityByName(trimmed)?.let { return it.latitude to it.longitude }
        val coords = parseCoordinatePair(trimmed) ?: return null
        return coords
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

    fun displayName(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return "—"
        cityByName(trimmed)?.let { return it.label }
        val coords = parseCoordinatePair(trimmed) ?: return trimmed
        return cityByCoordinates(coords.first, coords.second)?.label ?: trimmed
    }

    fun coordinatesOf(cityName: String): String? {
        val city = cityByName(cityName) ?: return null
        return "${city.latitude},${city.longitude}"
    }

    fun routeLabel(startRaw: String, endRaw: String): String =
        "${displayName(startRaw)} → ${displayName(endRaw)}"

    private fun cityByName(name: String): MapCity? =
        NorthStarMapCities.all.find { it.label.equals(name.trim(), ignoreCase = true) }

    private fun cityByCoordinates(latitude: Double, longitude: Double): MapCity? =
        NorthStarMapCities.all.find { city ->
            abs(city.latitude - latitude) < 0.08 && abs(city.longitude - longitude) < 0.08
        }
}
