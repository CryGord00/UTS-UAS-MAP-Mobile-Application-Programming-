package com.senorita.app.ui.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senorita.app.data.local.PreferencesManager
import kotlinx.coroutines.launch

class AuthViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {

    var isGuest by mutableStateOf(false)

    var phoneInput by mutableStateOf("")
    var phoneErrorState by mutableStateOf<String?>(null)

    var otpInput by mutableStateOf("")
    var otpErrorState by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    var loginEmail by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var loginEmailError by mutableStateOf<String?>(null)
    var loginPasswordError by mutableStateOf<String?>(null)

    var regName by mutableStateOf("")
    var regEmail by mutableStateOf("")
    var regPhone by mutableStateOf("")
    var regPassword by mutableStateOf("")
    var regNameError by mutableStateOf<String?>(null)
    var regEmailError by mutableStateOf<String?>(null)
    var regPhoneError by mutableStateOf<String?>(null)
    var regPasswordError by mutableStateOf<String?>(null)

    var otpCode by mutableStateOf("")
    var otpError by mutableStateOf<String?>(null)

    fun saveLocalRegistration() {
        preferencesManager.saveUserProfile(
            name = regName.ifBlank { "Pengguna SENORITA" },
            email = regEmail.ifBlank { "user@senorita.com" },
            phone = regPhone.ifBlank { "081234567890" }
        )
    }

    fun sendPhoneOtp(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val cleanPhone = phoneInput.trim()
        val formattedPhone = if (cleanPhone.startsWith("0")) {
            "+62${cleanPhone.substring(1)}"
        } else if (!cleanPhone.startsWith("+")) {
            "+62$cleanPhone"
        } else {
            cleanPhone
        }

        if (cleanPhone.isBlank()) {
            phoneErrorState = "Nomor handphone wajib diisi"
            return
        }
        if (cleanPhone.length < 10) {
            phoneErrorState = "Nomor handphone minimal 10 digit"
            return
        }

        phoneErrorState = null
        isLoading = true

        viewModelScope.launch {
            try {
                regPhone = formattedPhone
                isLoading = false
                onSuccess()
            } catch (_: Exception) {
                isLoading = false
                regPhone = formattedPhone
                onSuccess()
            }
        }
    }

    fun verifyPhoneOtp(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val code = if (otpInput.isNotBlank()) otpInput else otpCode
        if (code.length < 4) {
            otpErrorState = "Masukkan kode OTP valid (min 4-6 digit)"
            return
        }

        isLoading = true
        otpErrorState = null

        viewModelScope.launch {
            try {
                preferencesManager.saveUserProfile(
                    name = if (regName.isNotBlank()) regName else "Pengguna SENORITA",
                    email = if (regEmail.isNotBlank()) regEmail else "user@senorita.com",
                    phone = if (regPhone.isNotBlank()) regPhone else "+6281234567890"
                )
                isLoading = false
                onSuccess()
            } catch (_: Exception) {
                isLoading = false
                preferencesManager.saveUserProfile(
                    name = if (regName.isNotBlank()) regName else "Pengguna SENORITA",
                    email = if (regEmail.isNotBlank()) regEmail else "user@senorita.com",
                    phone = if (regPhone.isNotBlank()) regPhone else "+6281234567890"
                )
                onSuccess()
            }
        }
    }

    fun validateLogin(): Boolean {
        var isValid = true
        if (loginEmail.isBlank()) {
            loginEmailError = "Email wajib diisi"
            isValid = false
        } else {
            loginEmailError = null
        }
        if (loginPassword.isBlank()) {
            loginPasswordError = "Kata sandi wajib diisi"
            isValid = false
        } else {
            loginPasswordError = null
        }

        if (isValid) {
            preferencesManager.saveUserProfile(
                name = loginEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = loginEmail,
                phone = "081234567890"
            )
        }
        return isValid
    }

    fun validateRegister(): Boolean {
        var isValid = true

        // 1. Validate Name
        if (regName.isBlank()) {
            regNameError = "Nama lengkap wajib diisi"
            isValid = false
        } else {
            regNameError = null
        }

        // 2. Validate Email
        val cleanEmail = regEmail.trim()
        if (cleanEmail.isBlank()) {
            regEmailError = "Email wajib diisi"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            regEmailError = "Format email tidak valid (contoh: nama@email.com)"
            isValid = false
        } else {
            regEmailError = null
        }

        // 3. Validate Phone Number (min 10, max 12 digits)
        val cleanPhone = regPhone.trim()
        if (cleanPhone.isBlank()) {
            regPhoneError = "Nomor HP wajib diisi"
            isValid = false
        } else if (!cleanPhone.all { it.isDigit() }) {
            regPhoneError = "Nomor HP hanya boleh berisi angka"
            isValid = false
        } else if (cleanPhone.length < 10 || cleanPhone.length > 12) {
            regPhoneError = "Nomor HP minimal 10 digit dan maksimal 12 digit"
            isValid = false
        } else {
            regPhoneError = null
        }

        // 4. Validate Password (min 8 chars, 1 uppercase, 1 lowercase, 1 special char)
        val password = regPassword
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        if (password.isBlank()) {
            regPasswordError = "Kata sandi wajib diisi"
            isValid = false
        } else if (password.length < 8) {
            regPasswordError = "Kata sandi minimal 8 karakter"
            isValid = false
        } else if (!hasUppercase) {
            regPasswordError = "Kata sandi harus mengandung minimal 1 huruf besar"
            isValid = false
        } else if (!hasLowercase) {
            regPasswordError = "Kata sandi harus mengandung minimal 1 huruf kecil"
            isValid = false
        } else if (!hasSpecialChar) {
            regPasswordError = "Kata sandi harus mengandung minimal 1 karakter unik (contoh: #, $, !, ?)"
            isValid = false
        } else {
            regPasswordError = null
        }

        return isValid
    }

    fun validateOtp(): Boolean {
        return if (otpCode.length >= 4 && otpCode.all { it.isDigit() }) {
            otpError = null
            preferencesManager.saveUserProfile(
                name = if (regName.isNotBlank()) regName else "Pengguna SENORITA",
                email = if (regEmail.isNotBlank()) regEmail else "user@senorita.com",
                phone = if (regPhone.isNotBlank()) regPhone else "081234567890"
            )
            true
        } else {
            otpError = "Kode OTP harus 4-6 digit angka"
            false
        }
    }
}
