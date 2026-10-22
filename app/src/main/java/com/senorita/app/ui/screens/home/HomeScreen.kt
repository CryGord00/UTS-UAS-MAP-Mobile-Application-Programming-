package com.senorita.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.data.local.MockData
import com.senorita.app.data.model.MenuItem
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.MenuViewModel
import com.senorita.app.ui.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(
    menuViewModel: MenuViewModel? = null,
    profileViewModel: ProfileViewModel? = null,
    onNavigateToMenu: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToOutletLocation: () -> Unit = {},
    onNavigateToRewards: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        profileViewModel?.refreshProfile()
    }

    val userName = profileViewModel?.userProfile?.name
        ?.trim()
        ?.split(Regex("\\s+"))
        ?.firstOrNull()
        ?.ifBlank { "Pengguna" }
        ?: "Pengguna"
    val hasUnreadNotifications = MockData.notifications.any { !it.isRead }
    val promoListState = rememberLazyListState()
    val activePromo by remember {
        derivedStateOf { promoListState.firstVisibleItemIndex.coerceIn(PROMOS.indices) }
    }
    val bestSellerItems = remember { MockData.menuItems.filter { it.isPopular }.take(5) }
    val bestSellerListState = rememberLazyListState()
    val activeBestSeller by remember {
        derivedStateOf {
            bestSellerListState.firstVisibleItemIndex.coerceIn(0, (bestSellerItems.size - 1).coerceAtLeast(0))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = 20.dp, bottom = 22.dp)
        ) {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("SENORITA", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp), color = Color.White.copy(alpha = .75f))
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Hai, $userName 👋",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = .14f))
                                .clickable { onNavigateToOutletLocation() }
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Gading Serpong · 1.2 km",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BadgedBox(
                            badge = {
                                if (hasUnreadNotifications) {
                                    Badge(containerColor = MaterialTheme.colorScheme.error)
                                }
                            }
                        ) {
                            IconButton(
                                onClick = onNavigateToNotifications,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Notifications,
                                    contentDescription = if (hasUnreadNotifications) "Notifikasi baru" else "Notifikasi",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(20.dp))
            LazyRow(
                state = promoListState,
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(PROMOS) { _, promo ->
                    PromoSlide(promo = promo, onClick = onNavigateToMenu)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PROMOS.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(width = if (index == activePromo) 20.dp else 6.dp, height = 6.dp)
                            .clip(CircleShape)
                            .background(if (index == activePromo) Color.White else Color.White.copy(alpha = .42f))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Feature Buttons (Dine-in, Take Away, Find Store)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                "Dine-in" to "🍽️",
                "Take Away" to "🛍️",
                "Find Store" to "📍"
            ).forEach { (title, emoji) ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (title == "Find Store") onNavigateToOutletLocation() else onNavigateToMenu()
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pesanan Favorit Section
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onNavigateToMenu) {
                    Text(
                        text = "Lihat semua",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                state = bestSellerListState,
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(bestSellerItems) { item ->
                    PopularMenuItemCard(
                        item = item,
                        onClick = { onNavigateToDetail(item.id) }
                    )
                }
            }
            if (bestSellerItems.size > 1) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    bestSellerItems.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(width = if (index == activeBestSeller) 18.dp else 6.dp, height = 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == activeBestSeller) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline.copy(alpha = .7f)
                                )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

private data class HomePromo(
    val eyebrow: String,
    val title: String,
    val description: String,
    val emoji: String,
    val background: Color,
    val accent: Color
)

private val PROMOS = listOf(
    HomePromo("KHUSUS MEMBER", "Paket keluarga\nhemat 25%", "Makin seru dinikmati bersama", "🥘", Color(0xFF47243D), Color(0xFFFFC857)),
    HomePromo("PROMO HARI INI", "Diskon 20%\nmenu pilihan", "Temukan menu favoritmu", "🍜", Color(0xFF974B32), Color(0xFFFFD4A8)),
    HomePromo("REWARDS SENORITA", "Ngopi enak,\npoin berlipat", "Kumpulkan poin tiap pesanan", "🧋", Color(0xFF354B42), Color(0xFFCBE7B5))
)

@Composable
private fun PromoSlide(promo: HomePromo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(316.dp).height(145.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = promo.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(start = 18.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(color = promo.accent.copy(alpha = .22f), shape = RoundedCornerShape(50)) {
                    Text(
                        text = promo.eyebrow,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = promo.accent
                    )
                }
                Spacer(Modifier.height(9.dp))
                Text(
                    text = promo.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, lineHeight = 27.sp),
                    color = Color.White
                )
                Spacer(Modifier.height(5.dp))
                Text(promo.description, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = .8f))
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(78.dp).clip(CircleShape).background(Color.White.copy(alpha = .13f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = promo.emoji, fontSize = 43.sp)
            }
        }
    }
}

@Composable
fun PopularMenuItemCard(
    item: MenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.iconEmoji, fontSize = 52.sp)
                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(7.dp),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = .92f)
                ) {
                    Text(
                        text = "BEST SELLER",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.rating} (${item.reviewCount})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rp ${item.price.toInt()}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(
                    onClick = onClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Tambah",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    SENORITATheme {
        HomeScreen()
    }
}
