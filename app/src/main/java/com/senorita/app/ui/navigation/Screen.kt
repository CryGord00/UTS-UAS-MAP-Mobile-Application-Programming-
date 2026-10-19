package com.senorita.app.ui.navigation

sealed class Screen(val route: String, val title: String = "") {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Otp : Screen("otp")
    object Home : Screen("home", "Beranda")
    object MenuList : Screen("menu_list", "Menu")
    object MenuDetail : Screen("menu_detail/{itemId}", "Detail Menu") {
        fun createRoute(itemId: String) = "menu_detail/$itemId"
    }
    object Cart : Screen("cart", "Keranjang")
    object ChooseOutlet : Screen("choose_outlet", "Pilih Outlet & Meja")
    object ScanQr : Screen("scan_qr", "Scan QR Meja")
    object Payment : Screen("payment", "Pembayaran")
    object OrderTracking : Screen("order_tracking/{orderId}", "Status Pesanan") {
        fun createRoute(orderId: String) = "order_tracking/$orderId"
    }
    object OnlineReceipt : Screen("online_receipt/{orderId}", "Struk Online") {
        fun createRoute(orderId: String) = "online_receipt/$orderId"
    }
    object OrderHistory : Screen("order_history", "Pesanan Saya")
    object Rewards : Screen("rewards", "Rewards")
    object OutletLocation : Screen("outlet_location", "Lokasi Outlet")
    object Profile : Screen("profile", "Profil")
    object EditProfile : Screen("edit_profile", "Ubah Profil")
    object Notification : Screen("notification", "Notifikasi")
}
