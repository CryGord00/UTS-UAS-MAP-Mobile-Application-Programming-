package com.senorita.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaTextField
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateToOtp: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(Modifier.height(14.dp))
        AuthBrandHeader(
            title = "Buat akun SENORITA",
            subtitle = "Daftar sekarang dan nikmati keuntungan member di setiap pesanan."
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text("Data diri", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(10.dp))
                SenoritaTextField(
                    value = authViewModel.regName,
                    onValueChange = { authViewModel.regName = it },
                    label = "Nama Lengkap",
                    placeholder = "contoh: Resto Senorita",
                    errorMessage = authViewModel.regNameError,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                SenoritaTextField(
                    value = authViewModel.regEmail,
                    onValueChange = { authViewModel.regEmail = it },
                    label = "Alamat Email",
                    placeholder = "contoh: senorita@gmail.com",
                    errorMessage = authViewModel.regEmailError,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                SenoritaTextField(
                    value = authViewModel.regPhone,
                    onValueChange = { authViewModel.regPhone = it },
                    label = "Nomor Handphone",
                    placeholder = "10 - 12 digit (contoh: 081234567890)",
                    errorMessage = authViewModel.regPhoneError,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Phone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )

                SenoritaTextField(
                    value = authViewModel.regPassword,
                    onValueChange = { authViewModel.regPassword = it },
                    label = "Kata Sandi",
                    placeholder = "Min 8 karakter, 1 huruf besar, 1 kecil & 1 simbol",
                    errorMessage = authViewModel.regPasswordError,
                    isPassword = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                SenoritaButton(
                    text = "Daftar & Kirim OTP",
                    onClick = {
                        if (authViewModel.validateRegister()) {
                            authViewModel.saveLocalRegistration()
                            onNavigateToOtp()
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sudah punya akun? ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Masuk",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    val context = LocalContext.current
    val authViewModel = remember { AuthViewModel(PreferencesManager(context)) }
    SENORITATheme {
        RegisterScreen(
            authViewModel = authViewModel,
            onNavigateToOtp = {},
            onNavigateToLogin = {}
        )
    }
}
