package com.senorita.app.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.model.OrderType
import com.senorita.app.data.repository.SenoritaRepository
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaOutlinedButton
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.theme.SENORITATheme
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SenoritaTopAppBar(
                title = "Status Pesanan ",
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
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "ORDER #${order.id}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        Text(
                            text = order.outletName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )

                            Column {
                                Text(
                                    text = "Estimasi waktu tunggu",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )

                                Text(
                                    text = "${order.estimatedTimeMinutes} menit",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 18.dp,
                                vertical = 24.dp
                            )
                    ) {
                        val steps = listOf(
                            "Pesanan Diterima Dapur" to true,
                            "Sedang Dimasak & Disiapkan" to true,
                            "Siap Disajikan / Diambil" to false,
                            "Pesanan Selesai" to false
                        )

                        steps.forEachIndexed { index, (stepName, isDone) ->

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {

                                // TITIK DAN GARIS TIMELINE
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(
                                                color = if (isDone) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.surface
                                                },
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(20.dp),
                                            shape = CircleShape,
                                            color = if (isDone) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.surface
                                            },
                                            border = if (isDone) {
                                                null
                                            } else {
                                                androidx.compose.foundation.BorderStroke(
                                                    2.dp,
                                                    MaterialTheme.colorScheme.outlineVariant
                                                )
                                            }
                                        ) {
                                            if (isDone) {
                                                Box(
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Check,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(12.dp),
                                                        tint = MaterialTheme.colorScheme.onPrimary
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (index < steps.lastIndex) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(54.dp)
                                                .background(
                                                    if (isDone) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.outlineVariant
                                                    }
                                                )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(top = 1.dp)
                                ) {
                                    Text(
                                        text = stepName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isDone) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        },
                                        color = if (isDone) {
                                            MaterialTheme.colorScheme.onSurface
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )

                                    if (isDone) {
                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "Selesai",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    } else if (index == 2) {
                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "Menunggu",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (
                                        order.orderType == OrderType.DINE_IN
                                    ) {
                                        Icons.Filled.Restaurant
                                    } else {
                                        Icons.Filled.ShoppingBag
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (order.orderType == OrderType.DINE_IN) {
                                Text(
                                    text = "Meja ${order.tableNumber}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Tunjukkan nomor meja ini jika membutuhkan bantuan",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "Take Away",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Pesanan untuk diambil di outlet",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                SenoritaButton(
                    text = "Lihat Struk Online",
                    onClick = {
                        onNavigateToReceipt(order.id)
                    }
                )

                SenoritaOutlinedButton(
                    text = "Kembali ke Beranda",
                    onClick = onNavigateToHome
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderTrackingScreenPreview() {
    val context = LocalContext.current
    val prefManager = remember { PreferencesManager(context) }
    val repo = remember { SenoritaRepository(prefManager) }
    val orderViewModel = remember { OrderViewModel(repo) }
    SENORITATheme {
        OrderTrackingScreen(
            orderId = "SNR-8821",
            orderViewModel = orderViewModel,
            onNavigateToHome = {},
            onNavigateToReceipt = {}
        )
    }
}
