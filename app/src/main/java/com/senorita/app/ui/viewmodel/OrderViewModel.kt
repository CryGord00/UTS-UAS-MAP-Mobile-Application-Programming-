package com.senorita.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.senorita.app.data.model.Order
import com.senorita.app.data.model.OrderType
import com.senorita.app.data.model.Outlet
import com.senorita.app.data.repository.SenoritaRepository
import kotlinx.coroutines.flow.StateFlow

class OrderViewModel(val repository: SenoritaRepository) : ViewModel() {

    val orders: StateFlow<List<Order>> = repository.orders
    val selectedOutlet: StateFlow<Outlet> = repository.selectedOutlet
    val selectedOrderType: StateFlow<OrderType> = repository.selectedOrderType
    val selectedTableNumber: StateFlow<String> = repository.selectedTableNumber

    // Checkout Form State
    var customerNameInput by mutableStateOf("")
    var customerPhoneInput by mutableStateOf("")
    var tableNumberInput by mutableStateOf("")
    var selectedPaymentMethod by mutableStateOf("QRIS / GoPay")

    var nameError by mutableStateOf<String?>(null)
    var phoneError by mutableStateOf<String?>(null)
    var tableError by mutableStateOf<String?>(null)

    // Scanner Mock State
    var isFlashOn by mutableStateOf(false)
    var isSimulatingScan by mutableStateOf(false)

    fun initializeForm() {
        val profile = repository.getPreferencesManager().getUserProfile()
        customerNameInput = profile.name
        customerPhoneInput = profile.phone
        tableNumberInput = selectedTableNumber.value
    }

    fun validateOrderForm(): Boolean {
        var isValid = true

        if (customerNameInput.isBlank()) {
            nameError = "Nama pemesan wajib diisi"
            isValid = false
        } else {
            nameError = null
        }

        if (customerPhoneInput.isBlank()) {
            phoneError = "Nomor HP wajib diisi"
            isValid = false
        } else if (!customerPhoneInput.all { it.isDigit() }) {
            phoneError = "Nomor HP hanya boleh berisi angka"
            isValid = false
        } else if (customerPhoneInput.length < 10) {
            phoneError = "Nomor HP minimal 10 digit"
            isValid = false
        } else {
            phoneError = null
        }

        if (selectedOrderType.value == OrderType.DINE_IN && tableNumberInput.isBlank()) {
            tableError = "Nomor meja wajib diisi/dican"
            isValid = false
        } else {
            tableError = null
        }

        if (isValid) {
            repository.setTableNumber(tableNumberInput)
        }

        return isValid
    }

    fun setTableFromScan(tableNum: String) {
        tableNumberInput = tableNum
        repository.setTableNumber(tableNum)
    }

    fun submitOrder(): Order {
        return repository.createOrder(selectedPaymentMethod)
    }

    fun getOrderById(orderId: String): Order? {
        return repository.getOrderById(orderId)
    }
}
