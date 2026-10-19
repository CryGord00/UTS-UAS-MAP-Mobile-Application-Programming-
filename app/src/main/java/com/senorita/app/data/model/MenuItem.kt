package com.senorita.app.data.model

data class MenuItem(
    val id: String,
    val name: String,
    val category: String, // "Makanan", "Minuman", "Dessert", "Snack", "Promo"
    val price: Double,
    val description: String,
    val rating: Double,
    val reviewCount: Int,
    val iconEmoji: String = "🍽️",
    val sizes: List<Pair<String, Double>> = emptyList(),
    val spicinessLevels: List<String> = emptyList(),
    val temperatureOptions: List<String> = emptyList(),
    val addOns: List<Pair<String, Double>> = emptyList(),
    val isPopular: Boolean = false,
    val isPromo: Boolean = false,
    val discountPercent: Int = 0
)
