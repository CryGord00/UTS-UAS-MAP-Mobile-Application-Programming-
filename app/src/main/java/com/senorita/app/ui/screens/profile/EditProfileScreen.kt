package com.senorita.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.repository.SenoritaRepository
import com.senorita.app.ui.components.SenoritaButton
import com.senorita.app.ui.components.SenoritaTextField
import com.senorita.app.ui.components.SenoritaTopAppBar
import com.senorita.app.ui.theme.SENORITATheme
import com.senorita.app.ui.viewmodel.ProfileViewModel

@Composable
fun EditProfileScreen(
    profileViewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    LaunchedEffect(Unit) {
        profileViewModel.loadProfileForEdit()
    }

    Scaffold(
        topBar = {
            SenoritaTopAppBar(
                title = "Ubah Data Profil",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SenoritaTextField(
                value = profileViewModel.editName,
                onValueChange = { profileViewModel.editName = it },
                label = "Nama Lengkap",
                errorMessage = profileViewModel.editNameError,
                leadingIcon = { Icon(imageVector = Icons.Filled.Person, contentDescription = null) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            SenoritaTextField(
                value = profileViewModel.editEmail,
                onValueChange = { profileViewModel.editEmail = it },
                label = "Alamat Email",
                errorMessage = profileViewModel.editEmailError,
                leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )

            SenoritaTextField(
                value = profileViewModel.editPhone,
                onValueChange = { profileViewModel.editPhone = it },
                label = "Nomor Telepon (WhatsApp)",
                errorMessage = profileViewModel.editPhoneError,
                leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SenoritaButton(
                text = "Simpan Perubahan",
                onClick = {
                    if (profileViewModel.saveProfile()) {
                        onSaveSuccess()
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfileScreenPreview() {
    val context = LocalContext.current
    val prefManager = remember { PreferencesManager(context) }
    val repo = remember { SenoritaRepository(prefManager) }
    val profileViewModel = remember { ProfileViewModel(repo) }
    SENORITATheme {
        EditProfileScreen(
            profileViewModel = profileViewModel,
            onBackClick = {},
            onSaveSuccess = {}
        )
    }
}
