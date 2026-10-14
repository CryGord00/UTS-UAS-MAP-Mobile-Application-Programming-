package com.senorita.app.data.model

data class CartItem(
    val id: String,
    val menuItem: MenuItem,
    val selectedSize: String = "",
    val selectedSizePrice: Double = 0.0,
    val selectedSpiciness: String = "",
    val selectedTemperature: String = "",
    val selectedAddOns: List<Pair<String, Double>> = emptyList(),
    val specialNotes: String = "",
    val quantity: Int = 1
) {
    val unitPrice: Double
        get() = menuItem.price + selectedSizePrice + selectedAddOns.sumOf { it.second }

    val totalPrice: Double
        get() = unitPrice * quantity
}
