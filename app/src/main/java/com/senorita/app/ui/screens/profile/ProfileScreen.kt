package com.senorita.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.repository.SenoritaRepository
import com.senorita.app.ui.components.SenoritaOutlinedButton
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.ProfileViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onNavigateToOutletLocation: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val profile = profileViewModel.userProfile
    var showLogoutDialog by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes
     val formattedPoints = remember(profile.pointsBalance) {
        NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("id-ID")
        ).format(profile.pointsBalance)
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
             Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .height(64.dp)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                 Text(
                     text = "Profil",
                     style = MaterialTheme.typography.headlineMedium,
                     fontWeight = FontWeight.Bold,
                     color = MaterialTheme.colorScheme.onBackground
                )

             }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Foto profil",
                            tint = colors.onPrimaryContainer,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                           text = profile.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = profile.phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(
                                alpha = 0.85f
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "SENORITA Rewards", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "${profile.pointsBalance} Poin",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = onNavigateToRewards,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tukar Poin")
                    }
                }
            }

            Text(
                text = "Akun & Layanan",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ProfileOptionItem(
                        icon = Icons.Filled.PersonOutline,
                        title = "Personal Information",
                        onClick = onNavigateToEditProfile
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileOptionItem(
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        title = "Riwayat Pesanan Saya",
                        onClick = onNavigateToOrders
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileOptionItem(
                        icon = Icons.Filled.CardGiftcard,
                        title = "Katalog Rewards & Poin",
                        onClick = onNavigateToRewards
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileOptionItem(
                        icon = Icons.Filled.Store,
                        title = "Lokasi Outlet SENORITA",
                        onClick = onNavigateToOutletLocation
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileOptionItem(
                        icon = Icons.Filled.Edit,
                        title = "Ubah Data Profil",
                        onClick = onNavigateToEditProfile
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileOptionItem(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = "Help Center",
                        onClick = {},
                        enabled = false
                        )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SenoritaOutlinedButton(
                text = "Keluar Dari Akun (Logout)",
                onClick = { showLogoutDialog = true }
            )
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar dari akun SENORITA?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileViewModel.logout()
                        showLogoutDialog = false
                        onLogoutSuccess()
                    }
                ) {
                    Text("Keluar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    badge: String? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    val context = LocalContext.current
    val prefManager = remember { PreferencesManager(context) }
    val repo = remember { SenoritaRepository(prefManager) }
    val profileViewModel = remember { ProfileViewModel(repo) }
    SENORITATheme {
        ProfileScreen(
            profileViewModel = profileViewModel,
            onNavigateToEditProfile = {},
            onNavigateToOrders = {},
            onNavigateToRewards = {},
            onNavigateToOutletLocation = {},
            onLogoutSuccess = {}
        )
    }
}
