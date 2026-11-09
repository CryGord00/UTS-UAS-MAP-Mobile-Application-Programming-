package com.senorita.app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseOrder(
    @SerialName("id") val id: String,
    @SerialName("customer_name") val customerName: String? = null,
    @SerialName("customer_phone") val customerPhone: String? = null,
    @SerialName("outlet_name") val outletName: String,
    @SerialName("order_type") val orderType: String,
    @SerialName("table_number") val tableNumber: String? = null,
    @SerialName("subtotal") val subtotal: Double,
    @SerialName("tax_and_service") val taxAndService: Double,
    @SerialName("discount") val discount: Double = 0.0,
    @SerialName("total_amount") val totalAmount: Double,
    @SerialName("payment_method") val paymentMethod: String,
    @SerialName("status") val status: String = "PROCESSING"
)
