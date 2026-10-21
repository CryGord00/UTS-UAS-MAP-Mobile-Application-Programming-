package com.senorita.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.repository.SenoritaRepository
import com.senorita.app.ui.components.SenoritaOutlinedButton
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.ProfileViewModel

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

    Scaffold(
        topBar = {
            SenoritaTopAppBar(title = "Profil Saya")
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
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👤", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = profile.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = profile.phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    IconButton(
                        onClick = onNavigateToEditProfile,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                        icon = Icons.Filled.ReceiptLong,
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
    onClick: () -> Unit
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
