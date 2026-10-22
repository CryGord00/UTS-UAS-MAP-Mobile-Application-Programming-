package com.senorita.app.data.local

import androidx.compose.runtime.mutableStateListOf
import com.senorita.app.data.model.*

object MockData {
    val categories = listOf("Semua", "Appetizer", "Main Course", "Snack", "Beverages", "Dessert", "Promo")

    val menuItems = listOf(
        MenuItem(
            id = "menu_1",
            name = "Ayam Bakar SENORITA",
            category = "Main Course",
            price = 38000.0,
            description = "Ayam paha/dada pilihan yang dibakar dengan bumbu khas SENORITA bertabur wijen manis gurih, disajikan lengkap dengan nasi hangat, tahu, tempe, dan sambal terasi spesial.",
            rating = 4.9,
            reviewCount = 128,
            iconEmoji = "🍗",
            spicinessLevels = listOf("Tidak Pedas", "Sedang", "Pedas", "Extra Pedas"),
            addOns = listOf("Nasi Tambah" to 5000.0, "Tahu Tempe Extra" to 6000.0, "Sambal Extra" to 3000.0),
            isPopular = true,
            isPromo = true,
            discountPercent = 15
        ),
        MenuItem(
            id = "menu_2",
            name = "Nasi Goreng Senorita Special",
            category = "Main Course",
            price = 32000.0,
            description = "Nasi goreng rempah istimewa dengan potongan sosis, bakso, suwiran ayam, telor mata sapi, dan kerupuk renyah.",
            rating = 4.8,
            reviewCount = 95,
            iconEmoji = "🍛",
            spicinessLevels = listOf("Level 0", "Level 1", "Level 2", "Level 3"),
            addOns = listOf("Telur Ceplok" to 5000.0, "Sosis Extra" to 6000.0),
            isPopular = true
        ),
        MenuItem(
            id = "menu_3",
            name = "Es Kopi Susu Senorita",
            category = "Beverages",
            price = 22000.0,
            description = "Perpaduan espresso arabika, susu segar creamy, dan gula aren murni khas Nusantara.",
            rating = 4.9,
            reviewCount = 210,
            iconEmoji = "☕",
            sizes = listOf("Reguler" to 0.0, "Large" to 5000.0),
            temperatureOptions = listOf("Cold (Ice)", "Hot"),
            addOns = listOf("Extra Shot Espresso" to 6000.0, "Grass Jelly" to 4000.0, "Boba" to 5000.0),
            isPopular = true
        ),
        MenuItem(
            id = "menu_4",
            name = "Pink Matcha Latte",
            category = "Beverages",
            price = 28000.0,
            description = "Matcha Jepang Uji kualitas tinggi yang dipadu dengan sirup buah naga segar dan Milk Foam renyah.",
            rating = 4.7,
            reviewCount = 82,
            iconEmoji = "🍵",
            sizes = listOf("Reguler" to 0.0, "Large" to 6000.0),
            temperatureOptions = listOf("Cold (Ice)", "Hot"),
            addOns = listOf("Cheese Foam" to 5000.0, "Pudding" to 4000.0),
            isPromo = true,
            discountPercent = 10
        ),
        MenuItem(
            id = "menu_5",
            name = "Spaghetti Bolognese Cheese",
            category = "Main Course",
            price = 35000.0,
            description = "Pasta al dente dipadukan dengan saus daging sapi lezat dan taburan keju cheddar parut melimpah.",
            rating = 4.6,
            reviewCount = 64,
            iconEmoji = "🍝",
            addOns = listOf("Extra Keju Mozzarella" to 7000.0, "Garlic Bread" to 8000.0)
        ),
        MenuItem(
            id = "menu_6",
            name = "Choco Lava Cake & Ice Cream",
            category = "Dessert",
            price = 26000.0,
            description = "Kue cokelat hangat meluncur dengan cokelat leleh di dalam, disajikan dengan 1 scoop es krim vanila.",
            rating = 4.9,
            reviewCount = 142,
            iconEmoji = "🍫",
            addOns = listOf("Extra Scoop Ice Cream" to 8000.0),
            isPopular = true
        ),
        MenuItem(
            id = "menu_7",
            name = "Churros Cinnamon Sugar",
            category = "Dessert",
            price = 24000.0,
            description = "Churros garing bertabur gula kayu manis hangat dilengkapi saus cocolan dark chocolate.",
            rating = 4.7,
            reviewCount = 53,
            iconEmoji = "🥖",
            addOns = listOf("Saus Cokelat Extra" to 4000.0, "Saus Keju" to 4000.0)
        ),
        MenuItem(
            id = "menu_8",
            name = "French Fries Truffle Mayo",
            category = "Snack",
            price = 22000.0,
            description = "Kentang goreng renyah bumbu rosemary gurih disajikan dengan saus dip Truffle Mayonnaise.",
            rating = 4.8,
            reviewCount = 89,
            iconEmoji = "🍟",
            addOns = listOf("Saus Melted Cheese" to 5000.0)
        ),
        MenuItem(
            id = "menu_9", name = "Lumpia Ayam Sayur", category = "Appetizer", price = 18000.0,
            description = "Lumpia renyah berisi ayam cincang dan sayuran segar, disajikan dengan saus asam manis.",
            rating = 4.7, reviewCount = 48, iconEmoji = "🥟"
        ),
        MenuItem(
            id = "menu_10", name = "Tahu Cabe Garam", category = "Appetizer", price = 19000.0,
            description = "Tahu goreng lembut dengan bawang putih, cabai, dan daun bawang yang harum.",
            rating = 4.8, reviewCount = 76, iconEmoji = "🧈", isPopular = true
        ),
        MenuItem(
            id = "menu_11", name = "Chicken Wings BBQ", category = "Appetizer", price = 27000.0,
            description = "Sayap ayam panggang berbalut saus barbeque manis gurih.",
            rating = 4.8, reviewCount = 91, iconEmoji = "🍗"
        ),
        MenuItem(
            id = "menu_12", name = "Sup Krim Jagung", category = "Appetizer", price = 21000.0,
            description = "Sup jagung manis dan ayam dalam kuah krim hangat yang lembut.",
            rating = 4.6, reviewCount = 37, iconEmoji = "🍲"
        ),
        MenuItem(
            id = "menu_13", name = "Beef Teriyaki Rice Bowl", category = "Main Course", price = 39000.0,
            description = "Irisan daging sapi empuk dengan saus teriyaki, nasi hangat, dan sayuran.",
            rating = 4.9, reviewCount = 113, iconEmoji = "🍱", isPopular = true,
            addOns = listOf("Telur Onsen" to 7000.0, "Nasi Tambah" to 5000.0)
        ),
        MenuItem(
            id = "menu_14", name = "Salmon Mentai Rice", category = "Main Course", price = 46000.0,
            description = "Salmon panggang dengan saus mentai creamy di atas nasi hangat.",
            rating = 4.8, reviewCount = 68, iconEmoji = "🍣"
        ),
        MenuItem(
            id = "menu_15", name = "Sop Iga Rempah", category = "Main Course", price = 52000.0,
            description = "Iga sapi empuk dalam kuah kaldu rempah, disajikan dengan nasi dan sambal.",
            rating = 4.9, reviewCount = 102, iconEmoji = "🍖"
        ),
        MenuItem(
            id = "menu_16", name = "Chicken Katsu Curry", category = "Main Course", price = 37000.0,
            description = "Ayam katsu renyah dengan saus kari Jepang dan nasi pulen.",
            rating = 4.7, reviewCount = 83, iconEmoji = "🍛"
        ),
        MenuItem(
            id = "menu_17", name = "Mie Goreng Jawa", category = "Main Course", price = 30000.0,
            description = "Mie goreng berbumbu manis gurih dengan ayam, sayuran, dan telur.",
            rating = 4.6, reviewCount = 57, iconEmoji = "🍜"
        ),
        MenuItem(
            id = "menu_18", name = "Onion Rings", category = "Snack", price = 20000.0,
            description = "Irisan bawang bombai berlapis tepung renyah dengan saus cocolan.",
            rating = 4.5, reviewCount = 44, iconEmoji = "🧅"
        ),
        MenuItem(
            id = "menu_19", name = "Pisang Goreng Keju", category = "Snack", price = 21000.0,
            description = "Pisang goreng hangat dengan keju parut dan saus karamel.",
            rating = 4.8, reviewCount = 71, iconEmoji = "🍌", isPopular = true
        ),
        MenuItem(
            id = "menu_20", name = "Nachos Salsa", category = "Snack", price = 26000.0,
            description = "Keripik tortilla dengan saus keju, salsa tomat, dan jalapeño.",
            rating = 4.6, reviewCount = 39, iconEmoji = "🧀"
        ),
        MenuItem(
            id = "menu_21", name = "Lemon Tea", category = "Beverages", price = 16000.0,
            description = "Teh pilihan dengan perasan lemon segar, tersedia dingin atau hangat.",
            rating = 4.7, reviewCount = 94, iconEmoji = "🍋",
            temperatureOptions = listOf("Cold (Ice)", "Hot")
        ),
        MenuItem(
            id = "menu_22", name = "Chocolate Hazelnut", category = "Beverages", price = 25000.0,
            description = "Cokelat premium dengan sentuhan hazelnut dan susu segar.",
            rating = 4.8, reviewCount = 88, iconEmoji = "🍫",
            sizes = listOf("Reguler" to 0.0, "Large" to 5000.0),
            temperatureOptions = listOf("Cold (Ice)", "Hot")
        ),
        MenuItem(
            id = "menu_23", name = "Strawberry Yakult", category = "Beverages", price = 23000.0,
            description = "Minuman yogurt probiotik dengan rasa stroberi yang segar.",
            rating = 4.7, reviewCount = 62, iconEmoji = "🍓", isPopular = true
        ),
        MenuItem(
            id = "menu_24", name = "Lychee Mojito", category = "Beverages", price = 24000.0,
            description = "Soda ringan dengan leci, jeruk nipis, dan daun mint segar.",
            rating = 4.6, reviewCount = 51, iconEmoji = "🍹"
        ),
        MenuItem(
            id = "menu_25", name = "Panna Cotta Berry", category = "Dessert", price = 25000.0,
            description = "Panna cotta lembut disajikan dengan saus buah beri.",
            rating = 4.8, reviewCount = 59, iconEmoji = "🍮"
        ),
        MenuItem(
            id = "menu_26", name = "Waffle Maple Butter", category = "Dessert", price = 28000.0,
            description = "Waffle hangat dengan mentega dan sirup maple, pilihan tepat untuk berbagi.",
            rating = 4.7, reviewCount = 66, iconEmoji = "🧇"
        ),
        MenuItem(
            id = "menu_27", name = "Mango Pudding", category = "Dessert", price = 22000.0,
            description = "Puding mangga dingin dengan potongan buah mangga segar.",
            rating = 4.6, reviewCount = 42, iconEmoji = "🥭"
        )
    )

    val outlets = listOf(
        Outlet(
            id = "out_1",
            name = "SENORITA Gading Serpong",
            address = "Jl. Boulevard Raya Gading Serpong No. 18, Tangerang",
            distanceKm = 1.2,
            openingHours = "08:00 - 22:00 WIB",
            phoneNumber = "021-54321098",
            latitude = -6.2415,
            longitude = 106.6283
        ),
        Outlet(
            id = "out_2",
            name = "SENORITA BSD City",
            address = "Ruko Breeze BSD No. 5B, BSD City, Tangerang Selatan",
            distanceKm = 3.5,
            openingHours = "09:00 - 23:00 WIB",
            phoneNumber = "021-54329988",
            latitude = -6.3012,
            longitude = 106.6541
        ),
        Outlet(
            id = "out_3",
            name = "SENORITA Karawaci",
            address = "Taman Sari Lippo Karawaci Blok B12, Tangerang",
            distanceKm = 5.1,
            openingHours = "08:00 - 22:00 WIB",
            phoneNumber = "021-55881122",
            latitude = -6.2233,
            longitude = 106.6089
        )
    )

    val rewards = listOf(
        Reward(
            id = "rew_1",
            title = "Voucher Diskon Rp 15.000",
            pointsCost = 500,
            description = "Berlaku untuk semua pembelian dengan minimal belanja Rp 50.000.",
            discountAmount = 15000.0,
            validUntil = "31 Des 2026"
        ),
        Reward(
            id = "rew_2",
            title = "Gratis Es Kopi Susu Senorita",
            pointsCost = 800,
            description = "Tukarkan 800 poin untuk 1 cup Es Kopi Susu Senorita gratis.",
            discountAmount = 22000.0,
            validUntil = "31 Des 2026"
        ),
        Reward(
            id = "rew_3",
            title = "Voucher Diskon Rp 30.000",
            pointsCost = 1000,
            description = "Berlaku untuk Dine-In & Takeaway min pembelian Rp 100.000.",
            discountAmount = 30000.0,
            validUntil = "31 Des 2026"
        )
    )

    val pointTransactions = listOf(
        PointTransaction(
            id = "pt_1",
            title = "Cashback Pesanan #SNR-8821",
            points = 150,
            isEarned = true,
            date = "12 Okt 2026"
        ),
        PointTransaction(
            id = "pt_2",
            title = "Penukaran Voucher Diskon Rp 15.000",
            points = 500,
            isEarned = false,
            date = "10 Okt 2026"
        ),
        PointTransaction(
            id = "pt_3",
            title = "Welcome Bonus New Member",
            points = 1000,
            isEarned = true,
            date = "01 Okt 2026"
        )
    )

    val notifications = mutableStateListOf(
        NotificationItem(
            id = "notif_1",
            title = "Promo Spesial Hari Ini! ⚡",
            message = "Dapatkan diskon 15% untuk menu Ayam Bakar SENORITA. Jangan sampai kehabisan!",
            timestamp = "10 menit lalu",
            isRead = false,
            type = "Promo"
        ),
        NotificationItem(
            id = "notif_2",
            title = "Pesanan #SNR-8821 Selesai",
            message = "Terima kasih telah memesan di SENORITA Gading Serpong. Selamat menikmati!",
            timestamp = "2 jam lalu",
            isRead = true,
            type = "Order"
        ),
        NotificationItem(
            id = "notif_3",
            title = "Point Bonus +150 Diterima 🎉",
            message = "Poin reward dari transaksi Anda telah ditambahkan ke akun Anda.",
            timestamp = "1 hari lalu",
            isRead = true,
            type = "Promo"
        )
    )

    val initialOrders = listOf(
        Order(
            id = "SNR-8821",
            items = listOf(
                CartItem(
                    id = "c_1",
                    menuItem = menuItems[0],
                    selectedSpiciness = "Pedas",
                    selectedAddOns = listOf("Nasi Tambah" to 5000.0),
                    quantity = 2
                ),
                CartItem(
                    id = "c_2",
                    menuItem = menuItems[2],
                    selectedSize = "Reguler",
                    selectedTemperature = "Cold (Ice)",
                    quantity = 2
                )
            ),
            orderType = OrderType.DINE_IN,
            outletName = "SENORITA Gading Serpong",
            tableNumber = "Meja 08",
            subtotal = 130000.0,
            taxAndService = 13000.0,
            discount = 15000.0,
            totalAmount = 128000.0,
            paymentMethod = "QRIS / GoPay",
            status = OrderStatus.COMPLETED,
            customerName = "Dimas Pradhitya",
            customerPhone = "081234567890"
        )
    )
}
