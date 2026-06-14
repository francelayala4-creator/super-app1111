package com.superapp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

data class OsmMarker(
    val lat: Double,
    val lng: Double,
    val title: String? = null,
    val subtitle: String? = null,
)

// CartoDB Positron — tile source clean, minimalista, premium look
private val CARTODB_POSITRON = XYTileSource(
    "CartoDB.Positron", 1, 20, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/light_all/",
        "https://b.basemaps.cartocdn.com/light_all/",
        "https://c.basemaps.cartocdn.com/light_all/",
    ),
    "© OpenStreetMap, © CARTO"
)

@Composable
fun OsmMap(
    centerLat: Double,
    centerLng: Double,
    zoom: Double = 13.0,
    markers: List<OsmMarker> = emptyList(),
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current
    val mapView = remember(ctx) {
        Configuration.getInstance().apply {
            userAgentValue = ctx.packageName
            osmdroidBasePath = ctx.cacheDir
            osmdroidTileCache = java.io.File(ctx.cacheDir, "osmdroid_tiles")
        }
        MapView(ctx).apply {
            setTileSource(CARTODB_POSITRON)
            setMultiTouchControls(true)
            isHorizontalMapRepetitionEnabled = false
            isVerticalMapRepetitionEnabled = false
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        }
    }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose { mapView.onPause() }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { mapView },
        update = { mv ->
            mv.controller.setZoom(zoom)
            mv.controller.setCenter(GeoPoint(centerLat, centerLng))
            mv.overlays.clear()
            markers.forEach { m ->
                val marker = Marker(mv).apply {
                    position = GeoPoint(m.lat, m.lng)
                    title = m.title
                    snippet = m.subtitle
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mv.overlays.add(marker)
            }
            mv.invalidate()
        }
    )
}
