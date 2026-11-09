package com.senorita.app.data.repository

import android.util.Log
import com.senorita.app.data.local.MockData
import com.senorita.app.data.local.PreferencesManager
import com.senorita.app.data.model.*
import com.senorita.app.data.remote.SupabaseManager
import com.senorita.app.data.remote.model.SupabaseMenuItem
import com.senorita.app.data.remote.model.SupabaseOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SenoritaRepository(private val preferencesManager: PreferencesManager) {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _menuItems = MutableStateFlow<List<MenuItem>>(MockData.menuItems)
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

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
        // Fetch real-time menu items from Supabase, with automatic fallback to MockData
        fetchMenuItemsFromSupabase()

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

    fun fetchMenuItemsFromSupabase() {
        repositoryScope.launch {
            try {
                val remoteItems = SupabaseManager.postgrest["menu_items"]
                    .select()
                    .decodeList<SupabaseMenuItem>()

                if (remoteItems.isNotEmpty()) {
                    val mapped = remoteItems.map { remote ->
                        val localMatch = MockData.menuItems.find { it.id == remote.id }
                        MenuItem(
                            id = remote.id,
                            name = remote.name,
                            category = remote.category,
                            price = remote.price,
                            description = remote.description,
                            rating = remote.rating,
                            reviewCount = remote.reviewCount,
                            iconEmoji = remote.iconEmoji,
                            sizes = localMatch?.sizes ?: emptyList(),
                            spicinessLevels = localMatch?.spicinessLevels ?: emptyList(),
                            temperatureOptions = localMatch?.temperatureOptions ?: emptyList(),
                            addOns = localMatch?.addOns ?: emptyList(),
                            isPopular = remote.isPopular,
                            isPromo = remote.isPromo,
                            discountPercent = remote.discountPercent
                        )
                    }
                    _menuItems.value = mapped
                    Log.d("SenoritaRepository", "Loaded ${mapped.size} menus from Supabase successfully.")
                }
            } catch (e: Exception) {
                Log.e("SenoritaRepository", "Failed to fetch from Supabase, using local MockData fallback: ${e.message}")
            }
        }
    }

    fun getPreferencesManager(): PreferencesManager = preferencesManager

    fun getMenuItems(): List<MenuItem> = _menuItems.value

    fun getMenuItemById(id: String): MenuItem? =
        _menuItems.value.find { it.id == id } ?: MockData.menuItems.find { it.id == id }

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

        // Asynchronously sync order to Supabase
        repositoryScope.launch {
            try {
                val supabaseOrder = SupabaseOrder(
                    id = order.id,
                    customerName = order.customerName.ifBlank { null },
                    customerPhone = order.customerPhone.ifBlank { null },
                    outletName = order.outletName,
                    orderType = order.orderType.name,
                    tableNumber = order.tableNumber.ifBlank { null },
                    subtotal = order.subtotal,
                    taxAndService = order.taxAndService,
                    discount = order.discount,
                    totalAmount = order.totalAmount,
                    paymentMethod = order.paymentMethod,
                    status = order.status.name
                )
                SupabaseManager.postgrest["orders"].insert(supabaseOrder)
                Log.d("SenoritaRepository", "Synced order #${order.id} to Supabase successfully.")
            } catch (e: Exception) {
                Log.e("SenoritaRepository", "Failed to sync order to Supabase (saved locally): ${e.message}")
            }
        }

        // Auto-complete order 30 seconds after creation
        repositoryScope.launch {
            delay(30000L)
            completeOrder(order.id)
        }

        val pointsEarned = (total / 10000).toInt() * 10
        preferencesManager.addPoints(pointsEarned)
        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date())
        preferencesManager.addPointTransaction(
            PointTransaction(
                id = "pt_${System.currentTimeMillis()}",
                title = "Cashback Pesanan #${order.id}",
                points = pointsEarned,
                isEarned = true,
                date = currentDate
            )
        )

        clearCart()
        return order
    }

    fun getOrderById(orderId: String): Order? {
        return _orders.value.find { it.id == orderId }
    }
}
