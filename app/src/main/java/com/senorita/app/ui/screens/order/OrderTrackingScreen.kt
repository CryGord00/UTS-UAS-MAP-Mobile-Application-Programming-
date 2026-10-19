package com.senorita.app.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaOutlinedButton
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.viewmodel.OrderViewModel

@Composable
fun OrderTrackingScreen(
    orderId: String,
    orderViewModel: OrderViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToReceipt: (String) -> Unit
) {
    val order = orderViewModel.getOrderById(orderId)

    Scaffold(
        topBar = {
            SenoritaTopAppBar(
                title = "Status Pesanan Realtime",
                onBackClick = onNavigateToHome
            )
        }
    ) { paddingValues ->
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Pesanan tidak ditemukan.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "👨‍🍳", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pesanan Sedang Diproses!",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Estimasi selesai: ${order.estimatedTimeMinutes} Menit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "ID Pesanan: #${order.id}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Progres Pesanan",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        listOf(
                            "Pesanan Diterima Dapur" to true,
                            "Sedang Dimasak & Disiapkan" to true,
                            "Siap Disajikan / Diambil" to false,
                            "Pesanan Selesai" to false
                        ).forEachIndexed { index, (stepName, isDone) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = (index + 1).toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = stepName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (index < 3) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 13.dp)
                                        .width(2.dp)
                                        .height(20.dp)
                                        .background(if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                )
                            }
                        }
                    }
                }

                SenoritaButton(
                    text = "Lihat Struk Online",
                    onClick = { onNavigateToReceipt(order.id) }
                )

                SenoritaOutlinedButton(
                    text = "Kembali ke Beranda",
                    onClick = onNavigateToHome
                )
            }
        }
    }
}
