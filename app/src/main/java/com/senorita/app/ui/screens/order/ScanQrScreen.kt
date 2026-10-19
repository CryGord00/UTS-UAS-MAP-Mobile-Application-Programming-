package com.senorita.app.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.viewmodel.OrderViewModel

@Composable
fun ScanQrScreen(
    orderViewModel: OrderViewModel,
    onBackClick: () -> Unit,
    onScanComplete: () -> Unit
) {
    Scaffold(
        topBar = {
            SenoritaTopAppBar(
                title = "Scan QR Meja SENORITA",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { orderViewModel.isFlashOn = !orderViewModel.isFlashOn }) {
                        Icon(
                            imageVector = if (orderViewModel.isFlashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                            contentDescription = "Flashlight",
                            tint = if (orderViewModel.isFlashOn) Color.Yellow else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Arahkan kamera ke QR Code di meja Anda",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(120.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "UI Mockup Kamera - Scan Otomatis Aktif",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(32.dp))

                SenoritaButton(
                    text = "Simulasi QR Terdeteksi (Meja 08)",
                    onClick = {
                        orderViewModel.setTableFromScan("Meja 08")
                        onScanComplete()
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
