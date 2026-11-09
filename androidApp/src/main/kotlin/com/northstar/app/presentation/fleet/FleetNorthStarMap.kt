package com.northstar.app.presentation.fleet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

private val CityDotColor = android.graphics.Color.parseColor("#2F9E6B")
private val TransitDotColor = android.graphics.Color.parseColor("#C62828")
private val TransitLineColor = android.graphics.Color.parseColor("#E8941A")

@Composable
fun FleetNorthStarMap(
    transitRoutes: List<TransitRoutePins>,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var fitted by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner, mapView) {
        val map = mapView ?: return@DisposableEffect onDispose {}
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> map.onResume()
                Lifecycle.Event.ON_PAUSE -> map.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        map.onResume()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            map.onPause()
        }
    }

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                isTilesScaledToDpi = true
                minZoomLevel = 3.0
                maxZoomLevel = 18.0
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                controller.setZoom(4.0)
                controller.setCenter(GeoPoint(55.7558, 37.6173))
                mapView = this
            }
        },
        update = { map ->
            map.bindNorthStarOverlays(transitRoutes)
            if (!fitted) {
                map.fitToNorthStarContent(transitRoutes)
                fitted = true
            }
        },
        onRelease = { map ->
            map.onPause()
            map.onDetach()
            if (mapView === map) {
                mapView = null
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(248.dp)
            .clip(RoundedCornerShape(10.dp)),
    )
}

private fun MapView.bindNorthStarOverlays(routes: List<TransitRoutePins>) {
    overlays.removeAll { it is Marker || it is Polyline }
    val cityIcon = dotIcon(context, CityDotColor, diameterDp = 12)
    val transitIcon = dotIcon(context, TransitDotColor, diameterDp = 14)

    NorthStarMapCities.all.forEach { city ->
        overlays.add(
            Marker(this).apply {
                position = GeoPoint(city.latitude, city.longitude)
                title = city.label
                snippet = "Город автопарка"
                icon = cityIcon
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            },
        )
    }

    routes.forEach { route ->
        val start = GeoPoint(route.startLatitude, route.startLongitude)
        val end = GeoPoint(route.endLatitude, route.endLongitude)
        val plate = route.carNumber.ifBlank { "рейс" }
        overlays.add(
            Polyline().apply {
                setPoints(listOf(start, end))
                outlinePaint.color = TransitLineColor
                outlinePaint.strokeWidth = 6f
            },
        )
        overlays.add(
            Marker(this).apply {
                position = start
                title = "Погрузка $plate"
                snippet = "${route.startLatitude}, ${route.startLongitude}"
                icon = transitIcon
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            },
        )
        overlays.add(
            Marker(this).apply {
                position = end
                title = "Выгрузка $plate"
                snippet = "${route.endLatitude}, ${route.endLongitude}"
                icon = transitIcon
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            },
        )
    }
    invalidate()
}

private fun MapView.fitToNorthStarContent(routes: List<TransitRoutePins>) {
    val points = buildList {
        NorthStarMapCities.all.forEach { add(GeoPoint(it.latitude, it.longitude)) }
        routes.forEach { route ->
            add(GeoPoint(route.startLatitude, route.startLongitude))
            add(GeoPoint(route.endLatitude, route.endLongitude))
        }
    }
    if (points.size < 2) return
    val box = BoundingBox.fromGeoPoints(points)
    val padding = (48 * resources.displayMetrics.density).toInt()
    post {
        if (width > 0 && height > 0) {
            zoomToBoundingBox(box, false, padding)
        }
    }
}

private fun dotIcon(context: Context, color: Int, diameterDp: Int): Drawable {
    val density = context.resources.displayMetrics.density
    val size = (diameterDp * density).toInt().coerceAtLeast(12)
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val radius = size / 2f - density
    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = Paint.Style.FILL
    }
    val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = density
    }
    canvas.drawCircle(size / 2f, size / 2f, radius, fill)
    canvas.drawCircle(size / 2f, size / 2f, radius, stroke)
    return BitmapDrawable(context.resources, bmp)
}
