package com.senorita.app.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.senorita.app.data.repository.SenoritaRepository
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.CartViewModel
import com.senorita.app.ui.viewmodel.OrderViewModel

@Composable
fun PaymentScreen(
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    onBackClick: () -> Unit,
    onPaymentSuccess: (String) -> Unit
) {
    val selectedOutlet by orderViewModel.selectedOutlet.collectAsState()
    val selectedOrderType by orderViewModel.selectedOrderType.collectAsState()
    val selectedTableNumber by orderViewModel.selectedTableNumber.collectAsState()

    val paymentMethods = listOf(
        "QRIS / GoPay / OVO" to "📱",
        "ShopeePay / DANA" to "💳",
        "Virtual Account BCA / Mandiri" to "🏦",
        "Kartu Kredit / Debit" to "💳"
    )

    Scaffold(
        topBar = {
            SenoritaTopAppBar(
                title = "Halaman Pembayaran",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total yang dibayar", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Rp ${cartViewModel.getTotal().toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SenoritaButton(
                        text = "Bayar Sekarang",
                        onClick = {
                            val newOrder = orderViewModel.submitOrder()
                            onPaymentSuccess(newOrder.id)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = selectedOutlet.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tipe: ${if (selectedOrderType == com.senorita.app.data.model.OrderType.DINE_IN) "Dine In ($selectedTableNumber)" else "Take Away"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Pemesan: ${orderViewModel.customerNameInput} (${orderViewModel.customerPhoneInput})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "Pilih Metode Pembayaran",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            paymentMethods.forEach { (method, emoji) ->
                val isSelected = orderViewModel.selectedPaymentMethod == method
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { orderViewModel.selectedPaymentMethod = method },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { orderViewModel.selectedPaymentMethod = method },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = emoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = method,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎁", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Dapatkan Poin Rewards!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Anda akan mendapatkan +${(cartViewModel.getTotal() / 10000).toInt() * 10} poin dari transaksi ini.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PaymentScreenPreview() {
    val context = LocalContext.current
    val prefManager = remember { PreferencesManager(context) }
    val repo = remember { SenoritaRepository(prefManager) }
    val cartViewModel = remember { CartViewModel(repo) }
    val orderViewModel = remember { OrderViewModel(repo) }
    SENORITATheme {
        PaymentScreen(
            cartViewModel = cartViewModel,
            orderViewModel = orderViewModel,
            onBackClick = {},
            onPaymentSuccess = {}
        )
    }
}
