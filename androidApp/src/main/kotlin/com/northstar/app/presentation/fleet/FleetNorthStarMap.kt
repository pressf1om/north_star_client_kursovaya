package com.northstar.app.presentation.fleet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.northstar.app.ui.theme.Amber
import com.northstar.app.ui.theme.CreamText
import com.northstar.app.ui.theme.Navy
import com.northstar.app.ui.theme.StatusGreen

@Composable
fun FleetNorthStarMap(
    transitRoutes: List<TransitRoutePins>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(132.dp)
            .background(Navy),
    ) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val heightPx = with(LocalDensity.current) { maxHeight.toPx() }

        fun toPixel(fraction: Offset): Offset =
            Offset(fraction.x * widthPx, fraction.y * heightPx)

        Canvas(modifier = Modifier.fillMaxSize()) {
            transitRoutes.forEach { route ->
                val start = toPixel(
                    GeoMap.latLonToFraction(route.startLatitude, route.startLongitude),
                )
                val end = toPixel(
                    GeoMap.latLonToFraction(route.endLatitude, route.endLongitude),
                )
                drawLine(
                    color = Amber.copy(alpha = 0.55f),
                    start = start,
                    end = end,
                    strokeWidth = 2f,
                )
                drawCircle(color = StatusGreen, radius = 5f, center = start)
                drawCircle(color = Amber, radius = 5f, center = end, style = Stroke(width = 2f))
                drawCircle(color = Amber.copy(alpha = 0.35f), radius = 5f, center = end)
            }

            NorthStarMapCities.all.forEach { city ->
                val center = toPixel(GeoMap.latLonToFraction(city.latitude, city.longitude))
                drawCircle(color = Amber, radius = 4f, center = center)
            }
        }

        NorthStarMapCities.all.forEach { city ->
            val fraction = GeoMap.latLonToFraction(city.latitude, city.longitude)
            val xDp = maxWidth * fraction.x - 12.dp
            val yDp = maxHeight * fraction.y - 20.dp
            Text(
                text = city.label,
                color = CreamText,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.offset(x = xDp, y = yDp),
            )
        }
    }
}
