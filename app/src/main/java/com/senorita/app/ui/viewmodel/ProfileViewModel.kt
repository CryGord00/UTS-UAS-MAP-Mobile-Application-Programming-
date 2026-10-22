package com.senorita.app.ui.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.senorita.app.data.repository.SenoritaRepository

class ProfileViewModel(private val repository: SenoritaRepository) : ViewModel() {

    var userProfile by mutableStateOf(repository.getPreferencesManager().getUserProfile())

    // Edit Profile Form State
    var editName by mutableStateOf("")
    var editEmail by mutableStateOf("")
    var editPhone by mutableStateOf("")

    var editNameError by mutableStateOf<String?>(null)
    var editEmailError by mutableStateOf<String?>(null)
    var editPhoneError by mutableStateOf<String?>(null)

    fun refreshProfile() {
        userProfile = repository.getPreferencesManager().getUserProfile()
    }

    fun loadProfileForEdit() {
        refreshProfile()
        editName = userProfile.name
        editEmail = userProfile.email
        editPhone = userProfile.phone
    }

    fun saveProfile(): Boolean {
        var isValid = true

        if (editName.isBlank()) {
            editNameError = "Nama lengkap tidak boleh kosong"
            isValid = false
        } else {
            editNameError = null
        }

        if (editEmail.isBlank()) {
            editEmailError = "Email tidak boleh kosong"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(editEmail).matches()) {
            editEmailError = "Format email tidak valid"
            isValid = false
        } else {
            editEmailError = null
        }

        if (editPhone.isBlank()) {
            editPhoneError = "Nomor HP tidak boleh kosong"
            isValid = false
        } else if (!editPhone.all { it.isDigit() }) {
            editPhoneError = "Nomor HP hanya boleh angka"
            isValid = false
        } else if (editPhone.length < 10) {
            editPhoneError = "Nomor HP minimal 10 digit"
            isValid = false
        } else {
            editPhoneError = null
        }

        if (isValid) {
            repository.getPreferencesManager().saveUserProfile(editName, editEmail, editPhone)
            refreshProfile()
        }

        return isValid
    }

    fun logout() {
        repository.getPreferencesManager().logout()
        refreshProfile()
    }
}
