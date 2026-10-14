package com.senorita.app.data.model

data class Outlet(
    val id: String,
    val name: String,
    val address: String,
    val distanceKm: Double,
    val openingHours: String,
    val phoneNumber: String,
    val latitude: Double,
    val longitude: Double
)
