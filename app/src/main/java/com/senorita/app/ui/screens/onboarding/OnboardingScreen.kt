package com.senorita.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaOutlinedButton
import com.senorita.app.ui.theme.SENORITATheme

data class OnboardingPage(
    val title: String,
    val description: String,
    val emoji: String
)

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Pesan Favoritmu Dengan Mudah",
            description = "Nikmati kemudahan memesan makanan dan minuman lezat langsung dari meja atau untuk dibawa pulang.",
            emoji = "🍱"
        ),
        OnboardingPage(
            title = "Nikmati Promo Spesial Setiap Hari",
            description = "Dapatkan berbagai tawaran diskon menarik, voucher poin reward, dan hidangan gratis pilihan.",
            emoji = "🎁"
        ),
        OnboardingPage(
            title = "Setiap Pesanan, Dapatkan Lebih",
            description = "Kumpulkan poin SENORITA Rewards dari setiap transaksi dan tukarkan dengan hidangan favoritmu.",
            emoji = "👑"
        )
    )

    var currentPage by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Lewati",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = pages[currentPage].emoji,
                    fontSize = 80.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = pages[currentPage].title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = pages[currentPage].description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(10.dp)
                            .width(if (index == currentPage) 24.dp else 8.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(
                                if (index == currentPage) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentPage < pages.size - 1) {
                SenoritaButton(
                    text = "Lanjut",
                    onClick = { currentPage++ }
                )
            } else {
                SenoritaButton(
                    text = "Daftar Akun Baru",
                    onClick = onNavigateToRegister
                )
                SenoritaOutlinedButton(
                    text = "Sudah Punya Akun? Masuk",
                    onClick = onNavigateToLogin
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    SENORITATheme {
        OnboardingScreen(
            onNavigateToLogin = {},
            onNavigateToRegister = {}
        )
    }
}
