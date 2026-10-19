# SENORITA - Mobile Food & Beverage Ordering App

**Aplikasi Pemesanan Makanan & Minuman "SENORITA"**  
Dibuat untuk Ujian Tengah Semester (UTS) Mata Kuliah IF570L - Lab Mobile Application Programming  
Universitas Multimedia Nusantara (UMN)

---

## 📱 Deskripsi Aplikasi
**SENORITA** adalah aplikasi pemesanan makanan dan minuman modern berbasis Android yang mempermudah pelanggan untuk melakukan pemesanan baik secara **Dine In** (Makan Di Tempat dengan Scan QR Meja) maupun **Take Away** (Bawa Pulang). Aplikasi dilengkapi dengan sistem poin loyalty (**SENORITA Rewards**), pencarian dan penyaringan menu interaktif, kustomisasi pesanan, penukaran voucher, hingga pelacakan status pesanan secara real-time dan penerbitan struk digital.

---

## 👥 Anggota Kelompok & Pembagian Tugas

### Daftar Anggota Kelompok
1. **Dimas Pradhitya** (NIM: 00000126564) - *Lead Mobile Developer & UI/UX Specialist*

### Sistem Kerja Kelompok
Pengerjaan proyek dilakukan secara terstruktur menggunakan metodologi Agile dengan pendekatan komponen-komponen terpisah:
- **Analisis & Arsitektur**: Merancang flowchart navigasi, struktur data class, dan pola arsitektur MVVM.
- **UI & Design System**: Membuat custom Material 3 Theme (skema warna ungu `#800080` & aksen pink `#E91E63`), komponen reusable, serta tata letak Jetpack Compose.
- **State & Logic Integration**: Mengimplementasikan `StateFlow`, `mutableStateOf`, ViewModel, SharedPreferences, serta validasi form input real-time.
- **Testing & Dokumen**: Pengujian fitur, pembentukan build APK, dan penyusunan dokumentasi proyek.

### Kontribusi Spesifik Individu
- **Dimas Pradhitya**:
  - Mengatur konfigurasi proyek Android Studio, Gradle dependencies, dan manifest.
  - Membuat arsitektur MVVM (`SenoritaRepository`, `PreferencesManager`, `MockData`).
  - Mengimplementasikan seluruh ViewModel (`AuthViewModel`, `MenuViewModel`, `CartViewModel`, `OrderViewModel`, `RewardsViewModel`, `ProfileViewModel`).
  - Mengembangkan custom Material 3 Theme (`Color.kt`, `Type.kt`, `Shape.kt`, `Theme.kt`).
  - Mengimplementasikan 15+ layar Compose interaktif lengkap dengan navigasi (`NavHost`), pengiriman argumen, validasi form, dan UI mockup fitur native (Scan QR Meja & Peta Outlet).

---

## 🎯 Pemetaan Kriteria Rubrik UTS

### 1. Screen Implementation (Weight 15 - Excellent)
Aplikasi mengimplementasikan seluruh jenis layar yang dipersyaratkan secara presisi:
- **List Screen**: `MenuListScreen.kt` (Katalog menu dengan kategori tabs & pencarian real-time).
- **Detail Screen**: `MenuDetailScreen.kt` (Detail menu lengkap dengan kustomisasi ukuran, pedas, add-ons, & catatan khusus).
- **Form/Input Screen**: `LoginScreen.kt`, `RegisterScreen.kt`, `ChooseOutletScreen.kt` (Form input dengan keyboard khusus dan penanganan error).
- **Native-Feature UI Mockups**:
  - `ScanQrScreen.kt` (Mockup kamera pemindai QR Code Meja untuk Dine-In).
  - `OutletLocationScreen.kt` (Mockup peta lokasi outlet terdekat).
- **Free Choice Screens**: `SplashScreen.kt`, `OnboardingScreen.kt`, `HomeScreen.kt`, `CartScreen.kt`, `PaymentScreen.kt`, `OrderTrackingScreen.kt`, `OnlineReceiptScreen.kt`, `RewardsScreen.kt`, `ProfileScreen.kt`, `EditProfileScreen.kt`, `OrderHistoryScreen.kt`, `NotificationScreen.kt`.

### 2. UI Theming (Weight 15 - Excellent)
- Custom Material 3 Theme dengan identitas warna brand SENORITA:
  - **Primary**: Ungu (`#800080`)
  - **Secondary**: Pink Accent (`#E91E63`)
  - **Tertiary**: Gold Reward (`#FFB300`)
- Diterapkan secara konsisten di seluruh layar menggunakan `SENORITATheme`, `Typography`, dan `Shapes`.

### 3. State Management (Weight 15 - Excellent)
- Menggunakan pola MVVM bersih dengan `StateFlow`, `remember`, dan `mutableStateOf`.
- Mengelola state input form, state error validasi, state keranjang, pilihan kustomisasi item, status pesanan aktif, serta poin loyalty secara reaktif.

### 4. Navigation (Weight 15 - Excellent)
- Diimplementasikan menggunakan `NavHost` dan `NavController`.
- Menerapkan pengiriman argumen dinamis antar layar (contoh: `itemId` dari List ke Detail Screen, `orderId` dari Cart ke Tracking & Receipt Screen).

### 5. Form Input & Validation (Weight 15 - Excellent)
- **Tipe Keyboard Disesuaikan**:
  - Email: `KeyboardType.Email`
  - Nomor Handphone: `KeyboardType.Number` (Validasi numerik + minimal 10 digit)
  - Kode OTP: `KeyboardType.Number` (Validasi 4 digit)
- **Feedback Error Langsung**: Menggunakan `isError` dan `supportingText` merah jika bidang wajib diisi kosong atau format salah.
- Tombol eksekusi diblokir/divalidasi sebelum data dinyatakan valid.

### 6. Dummy Data Implementation (Weight 15 - Excellent)
- Menggunakan data class Kotlin yang terstruktur rapat (`MenuItem`, `CartItem`, `Order`, `Outlet`, `Reward`, `UserProfile`, `NotificationItem`).
- Dataset variatif dan realistis mencakup berbagai kategori hidangan, opsi add-ons, lokasi cabang, transaksi poin, dan riwayat pesanan.

---

## 🛠️ Cara Menjalankan Proyek & Build APK

1. Buka folder proyek di **Android Studio Ladybug / Jellyfish / 2026.1+**.
2. Pastikan JDK diatur ke Java 17 / 21.
3. Jalankan perintah Gradle untuk melakukan sync dan build APK:
   ```bash
   ./gradlew assembleDebug
   ```
4. File APK yang dihasilkan dapat ditemukan di:
   `app/build/outputs/apk/debug/app-debug.apk`
