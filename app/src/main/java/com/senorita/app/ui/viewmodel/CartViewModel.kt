package com.senorita.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.senorita.app.data.model.CartItem
import com.senorita.app.data.model.OrderType
import com.senorita.app.data.model.Outlet
import com.senorita.app.data.model.Reward
import com.senorita.app.data.repository.SenoritaRepository
import kotlinx.coroutines.flow.StateFlow

class CartViewModel(private val repository: SenoritaRepository) : ViewModel() {

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
    val selectedOutlet: StateFlow<Outlet> = repository.selectedOutlet
    val selectedOrderType: StateFlow<OrderType> = repository.selectedOrderType
    val selectedTableNumber: StateFlow<String> = repository.selectedTableNumber
    val appliedVoucher: StateFlow<Reward?> = repository.appliedVoucher

    private val availableVouchers = listOf(
        Reward("code_15", "Voucher SENORITA15", 0, "Promo Diskon Rp 15.000", 15000.0, "Hari ini"),
        Reward("code_30", "Voucher HEMAT30", 0, "Promo Diskon Rp 30.000", 30000.0, "Hari ini")
    )

    fun updateQuantity(cartItemId: String, delta: Int) {
        repository.updateCartQuantity(cartItemId, delta)
    }

    fun removeItem(cartItemId: String) {
        repository.removeCartItem(cartItemId)
    }

    fun setOrderType(orderType: OrderType) {
        repository.setOrderType(orderType)
    }

    fun selectOutlet(outlet: Outlet) {
        repository.selectOutlet(outlet)
    }

    fun getAvailableVouchers(): List<Reward> = availableVouchers

    fun selectVoucher(voucher: Reward?) {
        repository.applyVoucher(voucher)
    }

    fun removeAppliedVoucher() {
        repository.applyVoucher(null)
    }

    fun getSubtotal(): Double = cartItems.value.sumOf { it.totalPrice }

    fun getDiscount(): Double = appliedVoucher.value?.discountAmount ?: 0.0

    fun getTaxAndService(): Double = (getSubtotal() - getDiscount()).coerceAtLeast(0.0) * 0.1

    fun getTotal(): Double = (getSubtotal() - getDiscount()).coerceAtLeast(0.0) + getTaxAndService()

    fun getPointsEarned(): Int = (getTotal() / 10000).toInt() * 10
}
