package com.senorita.app.ui.screens.outlet

import android.graphics.Color
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.senorita.app.R
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.senorita.app.data.model.Outlet
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

private const val STYLE_URL = "https://styles.maptoolkit.org/summer.json"
private const val LOCATION_SOURCE = "senorita-location"

@Suppress("DEPRECATION")
@Composable
internal fun InteractiveOutletMap(outlets: List<Outlet>, position: Location?) {
    if (LocalInspectionMode.current) {
        DraggableMap()
        return
    }

    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val links = LocalUriHandler.current
    val mapView = remember(context, lifecycle) {
        MapLibre.getInstance(context)
        MapView(context).apply { onCreate(null) }
    }
    var map by remember(mapView) { mutableStateOf<MapLibreMap?>(null) }
    var source by remember(mapView) { mutableStateOf<GeoJsonSource?>(null) }
    var failed by remember(mapView) { mutableStateOf(false) }
    val coordinates = remember(outlets) { outlets.map { LatLng(it.latitude, it.longitude) } }
    val padding = (36 * context.resources.displayMetrics.density).toInt()

    fun showOutlets(target: MapLibreMap) {
        when {
            coordinates.size > 1 -> target.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    LatLngBounds.Builder().includes(coordinates).build(), padding
                )
            )
            coordinates.size == 1 -> target.moveCamera(
                CameraUpdateFactory.newLatLngZoom(coordinates.single(), 14.0)
            )
        }
    }

    DisposableEffect(mapView) {
        var active = true
        mapView.addOnDidFailLoadingMapListener { if (active) failed = true }
        mapView.getMapAsync { target ->
            if (active) target.setStyle(STYLE_URL) { style ->
                if (active) {
                    target.uiSettings.isLogoEnabled = false
                    val pin = checkNotNull(ContextCompat.getDrawable(context, R.drawable.ic_outlet_pin))
                    val icon = IconFactory.getInstance(context).fromBitmap(pin.toBitmap())
                    outlets.zip(coordinates).forEach { (outlet, point) ->
                        target.addMarker(MarkerOptions().position(point).icon(icon)
                            .title(outlet.name).snippet(outlet.address))
                    }
                    val locationSource = GeoJsonSource(LOCATION_SOURCE)
                    style.addSource(locationSource)
                    style.addLayer(CircleLayer("senorita-location-glow", LOCATION_SOURCE).withProperties(
                        circleRadius(16f), circleColor(Color.rgb(26, 115, 232)), circleOpacity(0.24f)
                    ))
                    style.addLayer(CircleLayer("senorita-location-dot", LOCATION_SOURCE).withProperties(
                        circleRadius(7f), circleColor(Color.rgb(26, 115, 232)),
                        circleStrokeColor(Color.WHITE), circleStrokeWidth(2f)
                    ))
                    source = locationSource
                    map = target
                    failed = false
                    mapView.post { if (active) showOutlets(target) }
                }
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            active = false
            lifecycle.removeObserver(observer)
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) mapView.onPause()
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) mapView.onStop()
            mapView.onDestroy()
        }
    }

    LaunchedEffect(source, position) {
        val features = position?.let {
            listOf(Feature.fromGeometry(Point.fromLngLat(it.longitude, it.latitude)))
        }.orEmpty()
        source?.setGeoJson(FeatureCollection.fromFeatures(features))
    }

    Box(Modifier.fillMaxWidth().height(240.dp).clip(RoundedCornerShape(16.dp))) {
        key(mapView) { AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize()) }
        val activeMap = map
        if (activeMap == null || failed) {
            DraggableMap()
            Text(
                if (failed) "Peta online belum tersedia" else "Memuat peta interaktif",
                Modifier.align(Alignment.TopCenter).background(MaterialTheme.colorScheme.surface).padding(4.dp),
                style = MaterialTheme.typography.labelSmall
            )
        } else {
            Column(
                Modifier.align(Alignment.CenterEnd).padding(end = 8.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                MapButton(Icons.Filled.Add, "Perbesar peta") {
                    activeMap.animateCamera(CameraUpdateFactory.zoomIn())
                }
                MapButton(Icons.Filled.Remove, "Perkecil peta") {
                    activeMap.animateCamera(CameraUpdateFactory.zoomOut())
                }
                MapButton(Icons.Filled.Refresh, "Tampilkan semua outlet") { showOutlets(activeMap) }
                position?.let { current ->
                    MapButton(Icons.Filled.MyLocation, "Pusatkan ke lokasi saya") {
                        activeMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            LatLng(current.latitude, current.longitude), 15.0
                        ))
                    }
                }
            }
            AsyncImage(
                model = "https://www.maptoolkit.org/assets/maptoolkit-attribution.png",
                contentDescription = "Maptoolkit",
                alignment = Alignment.CenterStart,
                modifier = Modifier.align(Alignment.BottomStart).padding(4.dp).width(100.dp)
                    .height(24.dp).clickable { links.openUri("https://www.maptoolkit.org/") }
            )
            Row(Modifier.align(Alignment.BottomEnd).background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 4.dp)) {
                Text("© Maptoolkit", Modifier.clickable {
                    links.openUri("https://www.maptoolkit.com/copyright/")
                }, style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.width(4.dp))
                Text("© OpenStreetMap", Modifier.clickable {
                    links.openUri("https://www.openstreetmap.org/copyright")
                }, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun MapButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    SmallFloatingActionButton(
        onClick = onClick,
        modifier = Modifier.size(36.dp),
        shape = androidx.compose.foundation.shape.CircleShape,
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp))
    }
}
