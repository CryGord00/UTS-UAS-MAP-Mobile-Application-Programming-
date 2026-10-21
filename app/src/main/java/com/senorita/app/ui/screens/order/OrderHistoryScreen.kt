package com.senorita.app.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.model.Order
import com.senorita.app.data.model.OrderStatus
import com.senorita.app.data.repository.SenoritaRepository
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.OrderViewModel

@Composable
fun OrderHistoryScreen(
    orderViewModel: OrderViewModel,
    onBackClick: () -> Unit,
    onNavigateToTracking: (String) -> Unit,
    onNavigateToReceipt: (String) -> Unit
) {
    val orders by orderViewModel.orders.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val activeOrders = orders.filter { it.status == OrderStatus.PROCESSING || it.status == OrderStatus.PENDING || it.status == OrderStatus.READY }
    val pastOrders = orders.filter { it.status == OrderStatus.COMPLETED || it.status == OrderStatus.CANCELLED }

    Scaffold(
        topBar = {
            SenoritaTopAppBar(
                title = "Pesanan Saya",
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
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Pesanan Aktif (${activeOrders.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Riwayat Pesanan") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val displayedOrders = if (selectedTab == 0) activeOrders else pastOrders

            if (displayedOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == 0) "Tidak ada pesanan aktif saat ini." else "Belum ada riwayat pesanan.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayedOrders) { order ->
                        OrderHistoryCard(
                            order = order,
                            onTrackClick = { onNavigateToTracking(order.id) },
                            onReceiptClick = { onNavigateToReceipt(order.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(
    order: Order,
    onTrackClick: () -> Unit,
    onReceiptClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (order.status == OrderStatus.COMPLETED) onReceiptClick() else onTrackClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.outletName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    color = if (order.status == OrderStatus.COMPLETED)
                        MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (order.status == OrderStatus.COMPLETED) "Selesai" else "Diproses",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (order.status == OrderStatus.COMPLETED)
                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${order.items.size} Menu • ${order.items.firstOrNull()?.menuItem?.name ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total Pembayaran", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "Rp ${order.totalAmount.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (order.status != OrderStatus.COMPLETED) {
                    Button(
                        onClick = onTrackClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Lacak Pesanan")
                    }
                }
                OutlinedButton(
                    onClick = onReceiptClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Lihat Struk")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderHistoryScreenPreview() {
    val context = LocalContext.current
    val prefManager = remember { PreferencesManager(context) }
    val repo = remember { SenoritaRepository(prefManager) }
    val orderViewModel = remember { OrderViewModel(repo) }
    SENORITATheme {
        OrderHistoryScreen(
            orderViewModel = orderViewModel,
            onBackClick = {},
            onNavigateToTracking = {},
            onNavigateToReceipt = {}
        )
    }
}
