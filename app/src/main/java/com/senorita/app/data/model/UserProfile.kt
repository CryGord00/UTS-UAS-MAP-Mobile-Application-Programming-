package com.senorita.app.data.model

data class UserProfile(
    val name: String,
    val email: String,
    val phone: String,
    val memberTier: String = "Gold Member",
    val pointsBalance: Int = 1380
)
