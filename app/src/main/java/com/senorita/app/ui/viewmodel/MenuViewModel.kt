package com.senorita.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.senorita.app.data.model.MenuItem
import com.senorita.app.data.repository.SenoritaRepository

class MenuViewModel(private val repository: SenoritaRepository) : ViewModel() {

    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("Semua")

    // Detail & Custom Order Selection State
    var selectedItem by mutableStateOf<MenuItem?>(null)
    var selectedSize by mutableStateOf("")
    var selectedSizePrice by mutableStateOf(0.0)
    var selectedSpiciness by mutableStateOf("")
    var selectedTemperature by mutableStateOf("")
    var selectedAddOns by mutableStateOf<List<Pair<String, Double>>>(emptyList())
    var specialNotes by mutableStateOf("")
    var quantity by mutableIntStateOf(1)

    fun getFilteredMenuItems(): List<MenuItem> {
        return repository.getMenuItems().filter { item ->
            val matchesCategory = (selectedCategory == "Semua") ||
                    (selectedCategory == "Promo" && item.isPromo) ||
                    item.category.equals(selectedCategory, ignoreCase = true)

            val matchesSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    fun loadMenuItemDetail(itemId: String) {
        val item = repository.getMenuItemById(itemId)
        selectedItem = item
        if (item != null) {
            selectedSize = item.sizes.firstOrNull()?.first ?: ""
            selectedSizePrice = item.sizes.firstOrNull()?.second ?: 0.0
            selectedSpiciness = item.spicinessLevels.firstOrNull() ?: ""
            selectedTemperature = item.temperatureOptions.firstOrNull() ?: ""
            selectedAddOns = emptyList()
            specialNotes = ""
            quantity = 1
        }
    }

    fun toggleAddOn(addOn: Pair<String, Double>) {
        val current = selectedAddOns.toMutableList()
        if (current.any { it.first == addOn.first }) {
            current.removeAll { it.first == addOn.first }
        } else {
            current.add(addOn)
        }
        selectedAddOns = current
    }

    fun calculateDetailTotal(): Double {
        val base = selectedItem?.price ?: 0.0
        val sizePrice = selectedSizePrice
        val addOnsTotal = selectedAddOns.sumOf { it.second }
        return (base + sizePrice + addOnsTotal) * quantity
    }

    fun addToCart(): Boolean {
        val item = selectedItem ?: return false
        repository.addToCart(
            menuItem = item,
            selectedSize = selectedSize,
            selectedSizePrice = selectedSizePrice,
            selectedSpiciness = selectedSpiciness,
            selectedTemperature = selectedTemperature,
            selectedAddOns = selectedAddOns,
            specialNotes = specialNotes,
            quantity = quantity
        )
        return true
    }
}
