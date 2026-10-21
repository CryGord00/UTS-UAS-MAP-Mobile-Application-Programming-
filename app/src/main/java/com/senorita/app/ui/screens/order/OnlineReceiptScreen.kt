package com.senorita.app.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.repository.SenoritaRepository
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.OrderViewModel

@Composable
fun OnlineReceiptScreen(
    orderId: String,
    orderViewModel: OrderViewModel,
    onNavigateToHome: () -> Unit
) {
    val order = orderViewModel.getOrderById(orderId)

    Scaffold(
        topBar = {
            SenoritaTopAppBar(
                title = "Struk Pembayaran Digital",
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
                Text(text = "Struk tidak ditemukan.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "💃", fontSize = 48.sp)
                        Text(
                            text = "SENORITA RESTO",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = order.outletName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "No. Transaksi", style = MaterialTheme.typography.bodySmall)
                            Text(text = "#${order.id}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Pemesan", style = MaterialTheme.typography.bodySmall)
                            Text(text = order.customerName, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Tipe", style = MaterialTheme.typography.bodySmall)
                            Text(text = "${order.orderType.name} ${order.tableNumber}", style = MaterialTheme.typography.bodySmall)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Metode Bayar", style = MaterialTheme.typography.bodySmall)
                            Text(text = order.paymentMethod, style = MaterialTheme.typography.bodySmall)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                        order.items.forEach { cartItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${cartItem.quantity}x ${cartItem.menuItem.name}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    if (cartItem.selectedSize.isNotBlank()) {
                                        Text(text = "Size: ${cartItem.selectedSize}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Text(
                                    text = "Rp ${cartItem.totalPrice.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Subtotal", style = MaterialTheme.typography.bodySmall)
                            Text(text = "Rp ${order.subtotal.toInt()}", style = MaterialTheme.typography.bodySmall)
                        }
                        if (order.discount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Diskon Voucher", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                Text(text = "-Rp ${order.discount.toInt()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Pajak & Layanan", style = MaterialTheme.typography.bodySmall)
                            Text(text = "Rp ${order.taxAndService.toInt()}", style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "TOTAL LUNAS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(
                                text = "Rp ${order.totalAmount.toInt()}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Terima kasih telah memesan di SENORITA!\nSimpan struk ini sebagai bukti pembayaran sah.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SenoritaButton(
                    text = "Kembali ke Beranda",
                    onClick = onNavigateToHome
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnlineReceiptScreenPreview() {
    val context = LocalContext.current
    val prefManager = remember { PreferencesManager(context) }
    val repo = remember { SenoritaRepository(prefManager) }
    val orderViewModel = remember { OrderViewModel(repo) }
    SENORITATheme {
        OnlineReceiptScreen(
            orderId = "SNR-8821",
            orderViewModel = orderViewModel,
            onNavigateToHome = {}
        )
    }
}
