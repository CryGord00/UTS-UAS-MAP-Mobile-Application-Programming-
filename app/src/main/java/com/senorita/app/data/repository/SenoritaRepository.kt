package com.senorita.app.data.repository

import com.senorita.app.data.local.MockData
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SenoritaRepository(private val preferencesManager: PreferencesManager) {

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(MockData.initialOrders)
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _selectedOutlet = MutableStateFlow<Outlet>(MockData.outlets[0])
    val selectedOutlet: StateFlow<Outlet> = _selectedOutlet.asStateFlow()

    private val _selectedOrderType = MutableStateFlow<OrderType>(OrderType.DINE_IN)
    val selectedOrderType: StateFlow<OrderType> = _selectedOrderType.asStateFlow()

    private val _selectedTableNumber = MutableStateFlow<String>("")
    val selectedTableNumber: StateFlow<String> = _selectedTableNumber.asStateFlow()

    private val _appliedVoucher = MutableStateFlow<Reward?>(null)
    val appliedVoucher: StateFlow<Reward?> = _appliedVoucher.asStateFlow()

    init {
        // Automatically complete active initial orders after 30 seconds
        repositoryScope.launch {
            delay(30000L)
            val updated = _orders.value.map { order ->
                if (order.status != OrderStatus.COMPLETED && order.status != OrderStatus.CANCELLED) {
                    order.copy(status = OrderStatus.COMPLETED)
                } else {
                    order
                }
            }
            _orders.value = updated
        }
    }

    fun getPreferencesManager(): PreferencesManager = preferencesManager

    fun getMenuItems(): List<MenuItem> = MockData.menuItems

    fun getMenuItemById(id: String): MenuItem? = MockData.menuItems.find { it.id == id }

    fun getOutlets(): List<Outlet> = MockData.outlets

    fun selectOutlet(outlet: Outlet) {
        _selectedOutlet.value = outlet
    }

    fun setOrderType(orderType: OrderType) {
        _selectedOrderType.value = orderType
    }

    fun setTableNumber(tableNumber: String) {
        _selectedTableNumber.value = tableNumber
    }

    fun addToCart(
        menuItem: MenuItem,
        selectedSize: String = "",
        selectedSizePrice: Double = 0.0,
        selectedSpiciness: String = "",
        selectedTemperature: String = "",
        selectedAddOns: List<Pair<String, Double>> = emptyList(),
        specialNotes: String = "",
        quantity: Int = 1
    ) {
        val currentList = _cartItems.value.toMutableList()
        val newItem = CartItem(
            id = "cart_${System.currentTimeMillis()}",
            menuItem = menuItem,
            selectedSize = selectedSize,
            selectedSizePrice = selectedSizePrice,
            selectedSpiciness = selectedSpiciness,
            selectedTemperature = selectedTemperature,
            selectedAddOns = selectedAddOns,
            specialNotes = specialNotes,
            quantity = quantity
        )
        currentList.add(newItem)
        _cartItems.value = currentList
    }

    fun updateCartQuantity(cartItemId: String, delta: Int) {
        val currentList = _cartItems.value.mapNotNull { item ->
            if (item.id == cartItemId) {
                val newQty = item.quantity + delta
                if (newQty > 0) item.copy(quantity = newQty) else null
            } else {
                item
            }
        }
        _cartItems.value = currentList
    }

    fun removeCartItem(cartItemId: String) {
        _cartItems.value = _cartItems.value.filter { it.id != cartItemId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _appliedVoucher.value = null
    }

    fun applyVoucher(reward: Reward?) {
        _appliedVoucher.value = reward
    }

    fun completeOrder(orderId: String) {
        val updated = _orders.value.map { order ->
            if (order.id == orderId && order.status != OrderStatus.COMPLETED && order.status != OrderStatus.CANCELLED) {
                order.copy(status = OrderStatus.COMPLETED)
            } else {
                order
            }
        }
        _orders.value = updated
    }

    fun createOrder(paymentMethod: String): Order {
        val cartList = _cartItems.value
        val subtotal = cartList.sumOf { it.totalPrice }
        val discount = _appliedVoucher.value?.discountAmount ?: 0.0
        val taxAndService = (subtotal - discount).coerceAtLeast(0.0) * 0.1
        val total = (subtotal - discount).coerceAtLeast(0.0) + taxAndService

        val order = Order(
            id = "SNR-${(1000..9999).random()}",
            items = cartList,
            orderType = _selectedOrderType.value,
            outletName = _selectedOutlet.value.name,
            tableNumber = if (_selectedOrderType.value == OrderType.DINE_IN) _selectedTableNumber.value else "",
            subtotal = subtotal,
            taxAndService = taxAndService,
            discount = discount,
            totalAmount = total,
            paymentMethod = paymentMethod,
            status = OrderStatus.PROCESSING,
            customerName = preferencesManager.getUserProfile().name,
            customerPhone = preferencesManager.getUserProfile().phone
        )

        val updatedOrders = _orders.value.toMutableList()
        updatedOrders.add(0, order)
        _orders.value = updatedOrders

        // Auto-complete order 30 seconds after creation
        repositoryScope.launch {
            delay(30000L)
            completeOrder(order.id)
        }

        val pointsEarned = (total / 10000).toInt() * 10
        preferencesManager.addPoints(pointsEarned)

        clearCart()
        return order
    }

    fun getOrderById(orderId: String): Order? {
        return _orders.value.find { it.id == orderId }
    }
}
