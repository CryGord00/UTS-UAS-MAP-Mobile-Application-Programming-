package com.senorita.app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseProfile(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("phone") val phone: String = "",
    @SerialName("member_tier") val memberTier: String = "Gold Member",
    @SerialName("points_balance") val pointsBalance: Int = 1380
)
