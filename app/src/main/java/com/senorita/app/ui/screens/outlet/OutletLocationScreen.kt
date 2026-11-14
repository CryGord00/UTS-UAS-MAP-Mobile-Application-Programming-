package com.senorita.app.ui.screens.outlet

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.senorita.app.R
import com.senorita.app.data.local.MockData
import com.senorita.app.data.model.Outlet
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.theme.SENORITATheme
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun OutletLocationScreen(
    onBackClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    var position by remember { mutableStateOf<Location?>(null) }
    var findingLocation by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }
    val cancellation = remember { CancellationTokenSource() }

    DisposableEffect(cancellation) {
        onDispose { cancellation.cancel() }
    }

    fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun hasLocationPermission(): Boolean =
        hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
            hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

    fun findLocation() {
        val precise = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (!precise && !hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            locationError = "Izinkan akses lokasi di Pengaturan untuk mencari outlet terdekat."
            return
        }

        findingLocation = true
        locationError = null
        val priority = if (precise) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }
        val request = CurrentLocationRequest.Builder()
            .setPriority(priority)
            .setMaxUpdateAgeMillis(0L)
            .setDurationMillis(15_000L)
            .build()
        try {
            LocationServices.getFusedLocationProviderClient(context)
                .getCurrentLocation(request, cancellation.token)
                .addOnCompleteListener { task ->
                    if (!cancellation.token.isCancellationRequested) {
                        findingLocation = false
                        val current = if (task.isSuccessful) task.result else null
                        if (current != null) {
                            position = current
                        } else {
                            locationError = "Lokasi belum ditemukan. Pastikan lokasi perangkat aktif, lalu coba lagi."
                        }
                    }
                }
        } catch (_: SecurityException) {
            findingLocation = false
            locationError = "Akses lokasi tidak tersedia. Cek izin aplikasi di Pengaturan."
        }
    }

    val locationPermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { findLocation() }

    LaunchedEffect(Unit) {
        if (!isPreview) {
            if (hasLocationPermission()) {
                findLocation()
            } else {
                locationPermissions.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    val distances = remember(position) {
        position?.let { current ->
            MockData.outlets.associate { outlet ->
                val meters = FloatArray(1)
                Location.distanceBetween(
                    current.latitude, current.longitude, outlet.latitude, outlet.longitude, meters
                )
                outlet.id to meters[0] / 1000.0
            }
        }.orEmpty()
    }

    Scaffold(
        topBar = {
            SenoritaTopAppBar(
                title = "Temukan SENORITA",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text("Cari area, kota, atau outlet")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Cari outlet"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            InteractiveOutletMap(MockData.outlets, position)

            TextButton(
                onClick = { findLocation() },
                enabled = !findingLocation,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (findingLocation) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.LocationOn, contentDescription = null)
                }
                Spacer(Modifier.width(8.dp))
                Text(when {
                    findingLocation -> "Mencari lokasi…"
                    position != null -> "Perbarui lokasi"
                    else -> "Coba lagi"
                })
            }

            val locationStatus = locationError ?: position?.let { current ->
                if (current.hasAccuracy()) {
                    "Akurasi lokasi: ${current.accuracy.roundToInt()} m"
                } else {
                    "Lokasi ditemukan"
                }
            }
            locationStatus?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (distances.isEmpty()) "Daftar Outlet" else "Outlet Terdekat",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val filteredOutlets = MockData.outlets.filter { outlet ->
                outlet.name.contains(searchQuery, ignoreCase = true) ||
                outlet.address.contains(searchQuery, ignoreCase = true)
            }.sortedBy { distances[it.id] }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOutlets, key = { it.id }) { outlet ->
                    OutletCard(outlet = outlet, distanceKm = distances[outlet.id])
                }
            }
        }
    }
}

@Composable
fun DraggableMap() {
    val initialScale = 1.6f
    var scale by remember { mutableFloatStateOf(initialScale) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var mapWidth by remember { mutableFloatStateOf(0f) }
    var mapHeight by remember { mutableFloatStateOf(0f) }

    fun limitOffset() {
        val maxX = mapWidth * (scale - 1f) / 2f
        val maxY = mapHeight * (scale - 1f) / 2f
        offsetX = offsetX.coerceIn(-maxX, maxX)
        offsetY = offsetY.coerceIn(-maxY, maxY)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F0F5))
            .onSizeChanged { size ->
                mapWidth = size.width.toFloat()
                mapHeight = size.height.toFloat()
                limitOffset()
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 4f)
                    offsetX += pan.x
                    offsetY += pan.y
                    limitOffset()
                }
            }
    ) {
        Image(
            painter = painterResource(R.drawable.peta),
            contentDescription = "Peta outlet SENORITA",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize().graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offsetX
                translationY = offsetY
            }
        )

        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    scale = (scale * 1.25f).coerceAtMost(4f)
                    limitOffset()
                },
                modifier = Modifier.size(40.dp),
                containerColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Perbesar peta")
            }
            FloatingActionButton(
                onClick = {
                    scale = (scale / 1.25f).coerceAtLeast(1f)
                    limitOffset()
                },
                modifier = Modifier.size(40.dp),
                containerColor = Color.White
            ) {
                Icon(Icons.Filled.Remove, contentDescription = "Perkecil peta")
            }
            FloatingActionButton(
                onClick = {
                    scale = initialScale
                    offsetX = 0f
                    offsetY = 0f
                },
                modifier = Modifier.size(40.dp),
                containerColor = Color.White
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Reset peta")
            }
        }
    }
}

@Composable
fun OutletCard(outlet: Outlet, distanceKm: Double? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = outlet.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                if (distanceKm != null) Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (distanceKm < 1.0) {
                            "${(distanceKm * 1000).roundToInt()} m"
                        } else {
                            String.format(Locale.forLanguageTag("id-ID"), "%,.1f km", distanceKm)
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = outlet.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Jam Buka: ${outlet.openingHours} • Telp: ${outlet.phoneNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Petunjuk Arah", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Hubungi Outlet", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OutletLocationScreenPreview() {
    SENORITATheme {
        OutletLocationScreen(
            onBackClick = {}
        )
    }
}
