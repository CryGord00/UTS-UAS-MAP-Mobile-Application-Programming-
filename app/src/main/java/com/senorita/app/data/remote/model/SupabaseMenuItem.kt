package com.senorita.app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseMenuItem(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("category") val category: String,
    @SerialName("price") val price: Double,
    @SerialName("description") val description: String = "",
    @SerialName("rating") val rating: Double = 4.8,
    @SerialName("review_count") val reviewCount: Int = 100,
    @SerialName("icon_emoji") val iconEmoji: String = "🍽️",
    @SerialName("is_popular") val isPopular: Boolean = false,
    @SerialName("is_promo") val isPromo: Boolean = false,
    @SerialName("discount_percent") val discountPercent: Int = 0
)
