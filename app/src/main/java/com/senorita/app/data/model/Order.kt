package com.senorita.app.data.model

enum class OrderType {
    DINE_IN, TAKEAWAY
}

enum class OrderStatus {
    PENDING, PROCESSING, READY, COMPLETED, CANCELLED
}

data class Order(
    val id: String,
    val items: List<CartItem>,
    val orderType: OrderType,
    val outletName: String,
    val tableNumber: String = "",
    val subtotal: Double,
    val taxAndService: Double,
    val discount: Double,
    val totalAmount: Double,
    val paymentMethod: String,
    val status: OrderStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val customerName: String = "",
    val customerPhone: String = "",
    val estimatedTimeMinutes: Int = 15
)
