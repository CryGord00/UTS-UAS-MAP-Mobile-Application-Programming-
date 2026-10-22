package com.senorita.app.ui.screens.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.repository.SenoritaRepository
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.OrderViewModel
import com.senorita.app.R


@Composable
fun OnlineReceiptScreen(
    orderId: String,
    orderViewModel: OrderViewModel,
    onNavigateToHome: () -> Unit
) {
    val order = orderViewModel.getOrderById(orderId)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SenoritaTopAppBar(
                title = "Struk Pembayaran",
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
                Text(
                    text = "Struk tidak ditemukan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                           Image(
                            painter = painterResource(
                                id = R.drawable.logo
                            ),
                            contentDescription = "Logo SENORITA",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(96.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "SENORITA RESTO",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = order.outletName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "PEMBAYARAN LUNAS",
                                modifier = Modifier.padding(
                                    horizontal = 18.dp,
                                    vertical = 8.dp
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        ReceiptDivider()

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Informasi Transaksi",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ReceiptInfoRow(
                            label = "No. Transaksi",
                            value = "#${order.id}",
                            isBold = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ReceiptInfoRow(
                            label = "Pemesan",
                            value = order.customerName
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ReceiptInfoRow(
                            label = "Tipe",
                            value = "${order.orderType.name} ${order.tableNumber}"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ReceiptInfoRow(
                            label = "Metode Bayar",
                            value = order.paymentMethod
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        ReceiptDivider()
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Detail Pesanan",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        order.items.forEach { cartItem ->

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "${cartItem.quantity}x ${cartItem.menuItem.name}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (cartItem.selectedSize.isNotBlank()) {
                                        Spacer(
                                            modifier = Modifier.height(4.dp)
                                        )

                                        Text(
                                            text = "Size: ${cartItem.selectedSize}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = "Rp ${cartItem.totalPrice.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ReceiptDivider()

                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Rincian Pembayaran",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ReceiptInfoRow(
                            label = "Subtotal",
                            value = "Rp ${order.subtotal.toInt()}"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        if (order.discount > 0) {
                            ReceiptInfoRow(
                                label = "Diskon Voucher",
                                value = "-Rp ${order.discount.toInt()}",
                                valueColor = MaterialTheme.colorScheme.secondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        ReceiptInfoRow(
                            label = "Pajak & Layanan",
                            value = "Rp ${order.taxAndService.toInt()}"
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        ReceiptDivider()
                        Spacer(modifier = Modifier.height(20.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "TOTAL LUNAS",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Rp ${order.totalAmount.toInt()}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Terima kasih telah memesan di SENORITA!",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Simpan struk ini sebagai bukti pembayaran sah.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                SenoritaButton(text = "Kembali ke Beranda",
                    onClick = onNavigateToHome
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ReceiptDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        thickness = 1.dp
    )
}
@Composable
private fun ReceiptInfoRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = value,
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
            textAlign = TextAlign.End,
            color = valueColor
        )
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
