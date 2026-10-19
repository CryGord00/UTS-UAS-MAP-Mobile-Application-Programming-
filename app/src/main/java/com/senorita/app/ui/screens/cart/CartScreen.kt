package com.senorita.app.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.data.model.CartItem
import com.senorita.app.data.model.OrderType
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaTextField
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.viewmodel.CartViewModel

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onNavigateToChooseOutlet: () -> Unit,
    onNavigateToMenu: () -> Unit
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val selectedOrderType by cartViewModel.selectedOrderType.collectAsState()
    val appliedVoucher by cartViewModel.appliedVoucher.collectAsState()

    Scaffold(
        topBar = {
            SenoritaTopAppBar(title = "Keranjang Pesanan")
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Pembayaran", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "Rp ${cartViewModel.getTotal().toInt()}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        SenoritaButton(
                            text = "Lanjut Pilih Outlet & Meja",
                            onClick = onNavigateToChooseOutlet
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Keranjang Kamu Masih Kosong",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Yuk pilih makanan & minuman favoritmu sekarang!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    SenoritaButton(
                        text = "Lihat Menu SENORITA",
                        onClick = onNavigateToMenu,
                        modifier = Modifier.width(220.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Opsi Tipe Pesanan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { cartViewModel.setOrderType(OrderType.DINE_IN) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedOrderType == OrderType.DINE_IN)
                                    MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🍽️", fontSize = 28.sp)
                                Text(
                                    text = "Dine In (Makan Di Tempat)",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (selectedOrderType == OrderType.DINE_IN) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (selectedOrderType == OrderType.DINE_IN)
                                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { cartViewModel.setOrderType(OrderType.TAKEAWAY) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedOrderType == OrderType.TAKEAWAY)
                                    MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🛍️", fontSize = 28.sp)
                                Text(
                                    text = "Take Away (Bawa Pulang)",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (selectedOrderType == OrderType.TAKEAWAY) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (selectedOrderType == OrderType.TAKEAWAY)
                                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Daftar Item (${cartItems.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(cartItems) { item ->
                    CartItemCard(
                        cartItem = item,
                        onQuantityChange = { delta -> cartViewModel.updateQuantity(item.id, delta) },
                        onRemove = { cartViewModel.removeItem(item.id) }
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Kode Promo / Voucher",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (appliedVoucher != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = appliedVoucher!!.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Hemat Rp ${appliedVoucher!!.discountAmount.toInt()}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                    TextButton(onClick = { cartViewModel.removeAppliedVoucher() }) {
                                        Text(text = "Hapus", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        SenoritaTextField(
                                            value = cartViewModel.promoInputCode,
                                            onValueChange = { cartViewModel.promoInputCode = it },
                                            label = "Kode Voucher",
                                            placeholder = "Coba: SENORITA15",
                                            errorMessage = cartViewModel.promoErrorState
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { cartViewModel.applyPromoCode() },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(text = "Gunakan")
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Rincian Pembayaran",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Subtotal", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = "Rp ${cartViewModel.getSubtotal().toInt()}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (appliedVoucher != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Diskon Voucher", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                                    Text(
                                        text = "-Rp ${cartViewModel.getDiscount().toInt()}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Pajak & Layanan (10%)", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = "Rp ${cartViewModel.getTaxAndService().toInt()}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Biaya",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Rp ${cartViewModel.getTotal().toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = cartItem.menuItem.iconEmoji, fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.menuItem.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                val details = mutableListOf<String>()
                if (cartItem.selectedSize.isNotBlank()) details.add("Ukuran: ${cartItem.selectedSize}")
                if (cartItem.selectedSpiciness.isNotBlank()) details.add("Pedas: ${cartItem.selectedSpiciness}")
                if (cartItem.selectedAddOns.isNotEmpty()) details.add("Addons: ${cartItem.selectedAddOns.joinToString { it.first }}")

                if (details.isNotEmpty()) {
                    Text(
                        text = details.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rp ${cartItem.totalPrice.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onQuantityChange(-1) },
                        modifier = Modifier
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Filled.Remove, contentDescription = "Kurang", modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = cartItem.quantity.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { onQuantityChange(1) },
                        modifier = Modifier
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Tambah", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
