package com.senorita.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaOutlinedButton
import com.senorita.app.ui.components.SenoritaTextField
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💃", fontSize = 52.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Masuk ke SENORITA",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Nikmati kemudahan pesanan makanan, akses promo, dan kumpulkan point.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.height(56.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🇮🇩 +62",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    SenoritaTextField(
                        value = authViewModel.phoneInput,
                        onValueChange = { authViewModel.phoneInput = it },
                        label = "Nomor handphone",
                        placeholder = "812 3456 7890",
                        errorMessage = authViewModel.phoneErrorState,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SenoritaButton(
                text = if (authViewModel.isLoading) "Mengirim OTP..." else "Lanjut",
                onClick = {
                    authViewModel.sendPhoneOtp(
                        onSuccess = { onLoginSuccess() },
                        onError = { }
                    )
                },
                enabled = !authViewModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "atau",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(16.dp))

            SenoritaOutlinedButton(
                text = "Lanjut dengan Google",
                onClick = { onLoginSuccess() }
            )
        }

        TextButton(
            onClick = { onLoginSuccess() },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Lanjut sebagai Guest",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    val context = LocalContext.current
    val authViewModel = remember { AuthViewModel(PreferencesManager(context)) }
    SENORITATheme {
        LoginScreen(
            authViewModel = authViewModel,
            onLoginSuccess = {},
            onNavigateToRegister = {}
        )
    }
}
