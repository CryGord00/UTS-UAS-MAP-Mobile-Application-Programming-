package com.senorita.app.data.model

data class Reward(
    val id: String,
    val title: String,
    val pointsCost: Int,
    val description: String,
    val discountAmount: Double,
    val validUntil: String,
    val category: String = "Voucher"
)

data class PointTransaction(
    val id: String,
    val title: String,
    val points: Int,
    val isEarned: Boolean,
    val date: String
)
