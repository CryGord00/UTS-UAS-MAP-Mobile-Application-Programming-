package com.senorita.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaTextField
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.AuthViewModel

@Composable
fun OtpVerificationScreen(
    authViewModel: AuthViewModel,
    onOtpSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Verifikasi nomor kamu",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Kode 6 digit telah dikirim melalui WhatsApp ke\n+62 ${authViewModel.phoneInput.ifEmpty { "812 **** 7890" }}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        SenoritaTextField(
            value = authViewModel.otpInput,
            onValueChange = { if (it.length <= 6) authViewModel.otpInput = it },
            label = "Masukkan Kode OTP (6 Digit)",
            placeholder = "482169",
            errorMessage = authViewModel.otpErrorState,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Belum menerima kode?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = { }) {
                Text(
                    text = "Kirim ulang dalam 00:42",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SenoritaButton(
            text = if (authViewModel.isLoading) "Memverifikasi..." else "Verifikasi",
            onClick = {
                authViewModel.verifyPhoneOtp(
                    onSuccess = { onOtpSuccess() },
                    onError = { }
                )
            },
            enabled = !authViewModel.isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔒", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Jangan bagikan kode OTP kepada siapa pun, termasuk staf SENORITA.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OtpVerificationScreenPreview() {
    val context = LocalContext.current
    val authViewModel = remember { AuthViewModel(PreferencesManager(context)) }
    SENORITATheme {
        OtpVerificationScreen(
            authViewModel = authViewModel,
            onOtpSuccess = {},
            onBackClick = {}
        )
    }
}
