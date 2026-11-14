package com.senorita.app.ui.screens.order

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Observer
import com.google.common.util.concurrent.ListenableFuture
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.repository.SenoritaRepository
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.OrderViewModel
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val CAMERA_PERMISSION_PREFERENCES = "camera_permission_preferences"
private const val CAMERA_PERMISSION_REQUESTED_KEY = "camera_permission_requested"

private enum class CameraPermissionStatus {
    GRANTED,
    NOT_REQUESTED,
    DENIED,
    PERMANENTLY_DENIED
}

private enum class CameraPreviewStatus {
    INITIALIZING,
    READY,
    ERROR
}

@Composable
fun ScanQrScreen(
    orderViewModel: OrderViewModel,
    onBackClick: () -> Unit,
    onScanComplete: () -> Unit
) {
    var tableNumber by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionPreferences = remember(context) {
        context.getSharedPreferences(CAMERA_PERMISSION_PREFERENCES, Context.MODE_PRIVATE)
    }

    var cameraPermissionGranted by remember(context) {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionRequestedBefore by remember(context) {
        mutableStateOf(permissionPreferences.getBoolean(CAMERA_PERMISSION_REQUESTED_KEY, false))
    }
    var permissionRequestInProgress by remember { mutableStateOf(false) }
    var permissionCheckVersion by remember { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionRequestInProgress = false
        cameraPermissionGranted = granted
    }

    val requestCameraPermission: () -> Unit = {
        permissionRequestedBefore = true
        permissionPreferences.edit()
            .putBoolean(CAMERA_PERMISSION_REQUESTED_KEY, true)
            .apply()
        permissionRequestInProgress = true
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Ask on first entry. If the user denies, the screen below explains how to retry or
    // open Settings when Android no longer allows another permission prompt.
    LaunchedEffect(cameraPermissionGranted, permissionRequestedBefore) {
        if (!cameraPermissionGranted && !permissionRequestedBefore && !permissionRequestInProgress) {
            requestCameraPermission()
        }
    }

    // Re-check permission when returning from Settings after a permanent denial.
    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                cameraPermissionGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                permissionCheckVersion++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val cameraPermissionStatus = remember(
        cameraPermissionGranted,
        permissionRequestedBefore,
        permissionCheckVersion
    ) {
        when {
            cameraPermissionGranted -> CameraPermissionStatus.GRANTED
            !permissionRequestedBefore -> CameraPermissionStatus.NOT_REQUESTED
            (context as? Activity)?.shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
            ) == true -> CameraPermissionStatus.DENIED
            else -> CameraPermissionStatus.PERMANENTLY_DENIED
        }
    }

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var previewStatus by remember { mutableStateOf(CameraPreviewStatus.INITIALIZING) }
    var previewError by remember { mutableStateOf<String?>(null) }
    var previewRetryCount by remember { mutableIntStateOf(0) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    LaunchedEffect(cameraPermissionGranted, previewRetryCount) {
        if (!cameraPermissionGranted) {
            cameraProvider = null
            previewStatus = CameraPreviewStatus.INITIALIZING
            previewError = null
            return@LaunchedEffect
        }

        previewStatus = CameraPreviewStatus.INITIALIZING
        previewError = null
        try {
            cameraProvider = ProcessCameraProvider.getInstance(context.applicationContext)
                .await(ContextCompat.getMainExecutor(context))
        } catch (exception: Exception) {
            cameraProvider = null
            previewStatus = CameraPreviewStatus.ERROR
            previewError = exception.localizedMessage ?: "Kamera tidak dapat dimulai."
        }
    }

    DisposableEffect(cameraProvider, previewView, lifecycleOwner, cameraPermissionGranted, previewRetryCount) {
        val provider = cameraProvider
        val view = previewView
        if (!cameraPermissionGranted || provider == null || view == null) {
            onDispose { }
        } else {
            val previewUseCase = Preview.Builder().build()
            var isBound = false
            val streamObserver = Observer<PreviewView.StreamState> { streamState ->
                if (streamState == PreviewView.StreamState.STREAMING) {
                    previewStatus = CameraPreviewStatus.READY
                    previewError = null
                } else if (streamState == PreviewView.StreamState.IDLE) {
                    previewStatus = CameraPreviewStatus.INITIALIZING
                }
            }
            view.previewStreamState.observe(lifecycleOwner, streamObserver)

            try {
                previewUseCase.setSurfaceProvider(view.surfaceProvider)
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    previewUseCase
                )
                isBound = true
                previewStatus = CameraPreviewStatus.INITIALIZING
            } catch (exception: Exception) {
                previewStatus = CameraPreviewStatus.ERROR
                previewError = exception.localizedMessage ?: "Kamera tidak dapat dimulai."
            }

            onDispose {
                view.previewStreamState.removeObserver(streamObserver)
                if (isBound) {
                    provider.unbind(previewUseCase)
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Kembali",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    text = "Pilih Meja",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                IconButton(
                    onClick = { orderViewModel.isFlashOn = !orderViewModel.isFlashOn }
                ) {
                    Icon(
                        imageVector = if (orderViewModel.isFlashOn) {
                            Icons.Filled.FlashOn
                        } else {
                            Icons.Filled.FlashOff
                        },
                        contentDescription = "Flashlight",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF21101F)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Scan QR meja untuk melanjutkan.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier.size(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cameraPermissionStatus == CameraPermissionStatus.GRANTED) {
                            AndroidView(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp)),
                                factory = { viewContext ->
                                    PreviewView(viewContext).apply {
                                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                                        scaleType = PreviewView.ScaleType.FILL_CENTER
                                    }
                                },
                                update = { previewView = it }
                            )

                            when (previewStatus) {
                                CameraPreviewStatus.INITIALIZING -> CameraLoadingOverlay()
                                CameraPreviewStatus.READY -> Unit
                                CameraPreviewStatus.ERROR -> CameraErrorOverlay(
                                    message = previewError ?: "Kamera tidak dapat dimulai.",
                                    onRetry = { previewRetryCount++ }
                                )
                            }
                        } else {
                            CameraPermissionContent(
                                status = cameraPermissionStatus,
                                requestInProgress = permissionRequestInProgress,
                                onRequestPermission = requestCameraPermission,
                                onOpenSettings = {
                                    val settingsIntent = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", context.packageName, null)
                                    )
                                    context.startActivity(settingsIntent)
                                }
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(24.dp)
                                )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = "Arahkan kamera ke QR di atas meja",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Atau masukkan nomor meja secara manual",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Nomor meja",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = tableNumber,
                            onValueChange = { tableNumber = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Contoh: A-12") },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.QrCodeScanner,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }

                    Button(
                        onClick = {
                            orderViewModel.setTableFromScan(tableNumber.trim())
                            onScanComplete()
                        },
                        enabled = tableNumber.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Lanjut",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraPermissionContent(
    status: CameraPermissionStatus,
    requestInProgress: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF21101F))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Akses kamera diperlukan",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = when (status) {
                CameraPermissionStatus.PERMANENTLY_DENIED ->
                    "Izinkan kamera dari Pengaturan aplikasi untuk menampilkan pratinjau."
                else -> "Izinkan kamera agar pratinjau langsung dapat ditampilkan."
            },
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))

        when {
            requestInProgress -> CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                strokeWidth = 3.dp
            )
            status == CameraPermissionStatus.PERMANENTLY_DENIED -> Button(
                onClick = onOpenSettings,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Buka Pengaturan", textAlign = TextAlign.Center)
            }
            else -> Button(
                onClick = onRequestPermission,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Izinkan kamera", textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun CameraLoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xCC21101F)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                strokeWidth = 3.dp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Menyiapkan kamera...",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CameraErrorOverlay(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xF221101F))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Kamera tidak tersedia",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 3
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onRetry,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("Coba lagi")
        }
    }
}

private suspend fun <T> ListenableFuture<T>.await(executor: Executor): T =
    suspendCancellableCoroutine { continuation ->
        addListener(
            {
                if (continuation.isActive) {
                    try {
                        continuation.resume(get())
                    } catch (exception: Exception) {
                        continuation.resumeWithException(exception)
                    }
                }
            },
            executor
        )
    }

@ComposePreview(showBackground = true, showSystemUi = true)
@Composable
fun ScanQrScreenPreview() {
    val context = LocalContext.current
    val prefManager = remember { PreferencesManager(context) }
    val repo = remember { SenoritaRepository(prefManager) }
    val orderViewModel = remember { OrderViewModel(repo) }
    SENORITATheme {
        ScanQrScreen(
            orderViewModel = orderViewModel,
            onBackClick = {},
            onScanComplete = {}
        )
    }
}
