package com.senorita.app.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.data.local.MockData
import com.senorita.app.data.model.OrderType
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaTextField
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.viewmodel.OrderViewModel

@Composable
fun ChooseOutletScreen(
    orderViewModel: OrderViewModel,
    onBackClick: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToPayment: () -> Unit
) {
    LaunchedEffect(Unit) {
        orderViewModel.initializeForm()
    }

    val selectedOutlet by orderViewModel.selectedOutlet.collectAsState()
    val selectedOrderType by orderViewModel.selectedOrderType.collectAsState()

    Scaffold(
        topBar = {
            SenoritaTopAppBar(
                title = "Konfirmasi Outlet & Pemesan",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    SenoritaButton(
                        text = if (selectedOrderType == OrderType.DINE_IN && orderViewModel.tableNumberInput.isBlank())
                            "Scan QR Meja Dulu" else "Lanjut ke Pembayaran",
                        onClick = {
                            if (orderViewModel.validateOrderForm()) {
                                if (selectedOrderType == OrderType.DINE_IN && orderViewModel.tableNumberInput.isBlank()) {
                                    onNavigateToScanQr()
                                } else {
                                    onNavigateToPayment()
                                }
                            }
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text(
                    text = "Pilih Outlet SENORITA",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))

                MockData.outlets.forEach { outlet ->
                    val isSelected = selectedOutlet.id == outlet.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { orderViewModel.repository.selectOutlet(outlet) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { orderViewModel.repository.selectOutlet(outlet) },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = outlet.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = outlet.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Jarak: ${outlet.distanceKm} km • ${outlet.openingHours}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            if (selectedOrderType == OrderType.DINE_IN) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Pilih / Scan Meja Anda",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onNavigateToScanQr,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Buka Kamera untuk Scan QR Meja")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        SenoritaTextField(
                            value = orderViewModel.tableNumberInput,
                            onValueChange = { orderViewModel.tableNumberInput = it },
                            label = "Atau Input Nomor Meja Manual",
                            placeholder = "contoh: Meja 08",
                            errorMessage = orderViewModel.tableError
                        )
                    }
                }
            }

            Column {
                Text(
                    text = "Informasi Pemesan",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))

                SenoritaTextField(
                    value = orderViewModel.customerNameInput,
                    onValueChange = { orderViewModel.customerNameInput = it },
                    label = "Nama Pemesan",
                    placeholder = "Nama Anda",
                    errorMessage = orderViewModel.nameError,
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(12.dp))

                SenoritaTextField(
                    value = orderViewModel.customerPhoneInput,
                    onValueChange = { orderViewModel.customerPhoneInput = it },
                    label = "Nomor Telepon (WhatsApp)",
                    placeholder = "081234567890",
                    errorMessage = orderViewModel.phoneError,
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Phone, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }
    }
}
