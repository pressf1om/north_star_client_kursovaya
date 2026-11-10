package com.northstar.app.presentation.fleet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
private val SelectedLineColor = android.graphics.Color.parseColor("#E8941A")
private val DimLineColor = android.graphics.Color.parseColor("#8AA4B5")

@Composable
fun FleetNorthStarMap(
    routes: List<FleetMapRoute>,
    selectedApplicationId: Int?,
    onRouteSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var fittedOverview by rememberSaveable { mutableStateOf(false) }
    val latestSelect by rememberUpdatedState(onRouteSelected)
    val selected = routes.find { it.applicationId == selectedApplicationId }

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

    Column(modifier = modifier.fillMaxWidth()) {
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
                map.bindNorthStarOverlays(routes, selectedApplicationId) { id ->
                    latestSelect(id)
                }
                if (selected != null) {
                    map.fitToRoute(selected)
                } else if (!fittedOverview) {
                    map.fitToOverview(routes)
                    fittedOverview = true
                }
            },
            onRelease = { map ->
                map.onPause()
                map.onDetach()
                if (mapView === map) {
                    mapView = null
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(248.dp)
                .clip(RoundedCornerShape(10.dp)),
        )
        if (selected != null) {
            Text(
                text = "${selected.startCity} → ${selected.endCity} · ${selected.carNumber}",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "Погрузка — ${selected.startCity}, выгрузка — ${selected.endCity}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp),
            )
        } else {
            Text(
                text = "Нажмите рейс в списке — на карте появится путь",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
        }
    }
}

private fun MapView.bindNorthStarOverlays(
    routes: List<FleetMapRoute>,
    selectedApplicationId: Int?,
    onRouteSelected: (Int) -> Unit,
) {
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
        val selected = route.applicationId == selectedApplicationId
        val plate = route.carNumber.ifBlank { "рейс" }
        overlays.add(
            Polyline().apply {
                setPoints(listOf(start, end))
                outlinePaint.color = if (selected) SelectedLineColor else DimLineColor
                outlinePaint.strokeWidth = if (selected) 10f else 4f
                setOnClickListener { _, _, _ ->
                    onRouteSelected(route.applicationId)
                    true
                }
            },
        )
        val startMarker = Marker(this).apply {
            position = start
            title = "Погрузка: ${route.startCity}"
            snippet = "$plate"
            icon = transitIcon
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            setOnMarkerClickListener { marker, _ ->
                onRouteSelected(route.applicationId)
                marker.showInfoWindow()
                true
            }
        }
        val endMarker = Marker(this).apply {
            position = end
            title = "Выгрузка: ${route.endCity}"
            snippet = "$plate"
            icon = transitIcon
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            setOnMarkerClickListener { marker, _ ->
                onRouteSelected(route.applicationId)
                marker.showInfoWindow()
                true
            }
        }
        overlays.add(startMarker)
        overlays.add(endMarker)
        if (selected) {
            startMarker.showInfoWindow()
        }
    }
    invalidate()
}

private fun MapView.fitToOverview(routes: List<FleetMapRoute>) {
    val points = buildList {
        NorthStarMapCities.all.forEach { add(GeoPoint(it.latitude, it.longitude)) }
        routes.forEach { route ->
            add(GeoPoint(route.startLatitude, route.startLongitude))
            add(GeoPoint(route.endLatitude, route.endLongitude))
        }
    }
    zoomToPoints(points)
}

private fun MapView.fitToRoute(route: FleetMapRoute) {
    zoomToPoints(
        listOf(
            GeoPoint(route.startLatitude, route.startLongitude),
            GeoPoint(route.endLatitude, route.endLongitude),
        ),
    )
}

private fun MapView.zoomToPoints(points: List<GeoPoint>) {
    if (points.size < 2) return
    val box = BoundingBox.fromGeoPoints(points)
    val padding = (56 * resources.displayMetrics.density).toInt()
    post {
        if (width > 0 && height > 0) {
            zoomToBoundingBox(box, true, padding)
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
