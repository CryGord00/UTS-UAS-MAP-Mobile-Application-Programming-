package com.senorita.app.ui.screens.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.data.local.MockData
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.model.OrderType
import com.senorita.app.data.repository.SenoritaRepository
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaTextField
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.CartViewModel
import com.senorita.app.ui.viewmodel.OrderViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
    val outlets = remember(orderViewModel) { orderViewModel.repository.getOutlets() }
    val pickupOptions = remember {
        val now = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        listOf("Secepatnya") +
            listOf(30L, 45L, 60L).map { minutes ->
                now.plusMinutes(minutes).format(formatter) }
    }
    var selectedPickupTime by rememberSaveable { mutableStateOf("Secepatnya") }
    var restaurantNote by rememberSaveable { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            SenoritaTopAppBar(
                title = "Pilih Tipe Pesanan",
                onBackClick = onBackClick
            )
        },

        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    SenoritaButton(
                        text = if (
                            selectedOrderType == OrderType.DINE_IN &&
                            orderViewModel.tableNumberInput.isBlank()
                        ) {
                            "Scan QR Meja Dulu"
                        } else {
                            "Lanjut ke Pembayaran"
                        },
                        onClick = {
                            // Validasi dan navigasi asli kelompok
                            if (orderViewModel.validateOrderForm()) {
                                if (
                                    selectedOrderType == OrderType.DINE_IN &&
                                    orderViewModel.tableNumberInput.isBlank()
                                ) {
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
    ) { paddingValues -> Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Pilih Tipe Pesanan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Mau menikmati hidangan di restoran atau dibawa pulang?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OrderTypeSelectionCard(
                    title = "Dine In",
                    subtitle = "Makan di restoran",
                    selected = selectedOrderType == OrderType.DINE_IN,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = if (
                                selectedOrderType == OrderType.DINE_IN
                            ) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        orderViewModel.repository.setOrderType(
                            OrderType.DINE_IN
                        )
                    }
                )

                OrderTypeSelectionCard(
                    title = "Take Away",
                    subtitle = "Ambil di counter",
                    selected = selectedOrderType == OrderType.TAKEAWAY,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = if (
                                selectedOrderType == OrderType.TAKEAWAY
                            ) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        orderViewModel.repository.setOrderType(
                            OrderType.TAKEAWAY
                        )
                    }
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = if (
                        selectedOrderType == OrderType.DINE_IN
                    ) {
                        "Dine In dipilih. Pilih outlet, lalu masukkan nomor meja atau scan QR."
                    } else {
                        "Take Away dipilih. Pilih outlet tempat pesanan akan diambil."
                    },
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Pilih Outlet SENORITA",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                outlets.forEach { outlet ->
                    val isSelected = selectedOutlet.id == outlet.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                orderViewModel.repository.selectOutlet(
                                    outlet
                                )
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    orderViewModel.repository.selectOutlet(
                                        outlet
                                    )
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = outlet.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = outlet.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "Jarak: ${outlet.distanceKm} km",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = outlet.openingHours,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            if (selectedOrderType == OrderType.DINE_IN) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Nomor Meja",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Masukkan nomor meja atau gunakan scan QR.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        SenoritaTextField(
                            value = orderViewModel.tableNumberInput,
                            onValueChange = {
                                orderViewModel.tableNumberInput = it
                            },
                            label = "Nomor Meja",
                            placeholder = "Contoh: A-12",
                            errorMessage = orderViewModel.tableError
                        )

                        OutlinedButton(
                            onClick = onNavigateToScanQr,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Scan QR Meja", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Atur Pengambilan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Pesanan akan diambil di outlet yang kamu pilih.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Text(
                            text = "Lokasi pengambilan",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = selectedOutlet.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = selectedOutlet.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Jam operasional: ${selectedOutlet.openingHours}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Text(
                            text = "Waktu Pengambilan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Pilih waktu yang diinginkan untuk mengambil pesanan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedPickupTime == "Secepatnya",
                                    onClick = {
                                        selectedPickupTime = "Secepatnya"
                                    },
                                    label = {
                                        Text("Secepatnya")
                                    }
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                pickupOptions.drop(1).forEach { time ->
                                    FilterChip(
                                        selected = selectedPickupTime == time,
                                        onClick = {
                                            selectedPickupTime = time
                                        },
                                        label = {
                                            Text(time)
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor =
                                                MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor =
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Waktu dipilih: $selectedPickupTime",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedTextField(
                            value = restaurantNote,
                            onValueChange = {
                                restaurantNote = it
                            },
                            label = {
                                Text("Catatan untuk Restoran (Opsional)")
                            },
                            placeholder = {
                                Text("Contoh: Tanpa alat makan plastik")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text(
                            text = "Informasi pengambilan akan mengikuti pesanan di outlet ini.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Informasi Pemesan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Data diambil dari profil dan dapat diedit untuk pesanan ini.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                SenoritaTextField(
                    value = orderViewModel.customerNameInput,
                    onValueChange = {
                        orderViewModel.customerNameInput = it
                    },
                    label = "Nama Pemesan",
                    placeholder = "Nama Anda",
                    errorMessage = orderViewModel.nameError,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )

                SenoritaTextField(
                    value = orderViewModel.customerPhoneInput,
                    onValueChange = {
                        orderViewModel.customerPhoneInput = it
                    },
                    label = "Nomor Telepon (WhatsApp)",
                    placeholder = "081234567890",
                    errorMessage = orderViewModel.phoneError,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Phone,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun OrderTypeSelectionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(174.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            1.dp,
            if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            if (selected) {
                Spacer(modifier = Modifier.height(8.dp))

                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Dipilih",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChooseOutletScreenPreview() {
    val context = LocalContext.current
    val prefManager = remember { PreferencesManager(context) }
    val repo = remember { SenoritaRepository(prefManager) }
    val orderViewModel = remember { OrderViewModel(repo) }
    SENORITATheme {
        ChooseOutletScreen(
            orderViewModel = orderViewModel,
            onBackClick = {},
            onNavigateToScanQr = {},
            onNavigateToPayment = {}
        )
    }
}

