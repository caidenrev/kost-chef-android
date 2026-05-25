<p align="center">
  <img src="MASAKIN.png" alt="MASAKIN by Revan" width="320">
</p>

<h1 align="center">MASAKIN</h1>
<p align="center"><strong>Makan enak meski akhir bulan</strong></p>
<p align="center">Aplikasi Android untuk perencanaan masak, budget Rupiah, dan generator resep berbasis AI.</p>
<p align="center">Dibuat oleh <strong>Revan</strong></p>

<p align="center">
  <a href="https://github.com/caidenrev/kost-chef-android/raw/main/releases/MASAKIN-v1.0.apk"><strong>Unduh APK (v1.0)</strong></a>
  &nbsp;|&nbsp;
  <a href="https://github.com/caidenrev/kost-chef-android/releases">Semua Rilis</a>
</p>

---

## Unduh APK

Instal langsung di HP Android tanpa Play Store (APK **release signed**, siap pasang).

| | |
|---|---|
| **Versi** | 1.0 (`versionCode` 1) |
| **Ukuran** | ~16–22 MB |
| **Min. Android** | 7.0 (API 24) |
| **Package** | `com.example.chef_ai_revan` |
| **Unduh langsung** | [MASAKIN-v1.0.apk](https://github.com/caidenrev/kost-chef-android/raw/main/releases/MASAKIN-v1.0.apk) |
| **Halaman rilis** | [GitHub Releases](https://github.com/caidenrev/kost-chef-android/releases) |

### Cara instal di HP

1. **Hapus dulu** app MASAKIN lama jika pernah terpasang (Settings > Apps > MASAKIN > Uninstall). Wajib jika sebelumnya install dari Android Studio / APK lain.
2. Unduh `MASAKIN-v1.0.apk` sampai selesai (cek ukuran file, jangan ~1 KB — itu berarti unduhan gagal).
3. Buka file dari **Files** atau **Downloads** (bukan hanya preview di browser).
4. Aktifkan **Install unknown apps** / **Sumber tidak dikenal** untuk app yang dipakai buka APK.
5. Tap **Install**. Jika muncul Play Protect, tap **Install anyway** / **Tetap instal**.
6. Buka MASAKIN > Settings (ikon kuning) > masukkan Gemini API Key.

### APK tidak mau instal?

| Gejala | Solusi |
|--------|--------|
| "App not installed" | Uninstall versi lama dulu; unduh ulang APK |
| "Package conflicts" | Ada instalasi lama dengan signature beda — uninstall semua versi MASAKIN |
| File cuma beberapa KB | Link GitHub belum di-push; unduh ulang dari repo yang sudah update |
| Play Protect memblokir | Tap **More details** > **Install anyway** |
| Parse error | File corrupt — unduh ulang, jangan kirim lewat WhatsApp (compress) |

> Build ulang APK release: `.\gradlew.bat assembleRelease` lalu salin ke `releases/MASAKIN-v1.0.apk`

---

## Ringkasan

MASAKIN adalah aplikasi Android native (Kotlin + Jetpack Compose) dengan antarmuka Neo-Brutalism. Data pengguna disimpan **lokal di perangkat** (Room SQLite + DataStore). Fitur AI memakai **Google Gemini API Key** milik pengguna yang dimasukkan lewat tombol Settings di header.

| Aspek | Keterangan |
|-------|------------|
| Platform | Android 7.0+ (API 24) |
| Bahasa | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Database | Room SQLite (offline) |
| AI | Google Gemini API (key oleh user) |
| Arsitektur | MVVM + Repository |

---

## Fitur Utama

| Fitur | Deskripsi | Layar |
|-------|-----------|-------|
| AI Generator | Generate resep dari bahan terpilih + limit budget (IDR) | Menu |
| Wishlist | Simpan resep favorit dari hasil AI | Menu |
| Jadwal Mingguan | Alokasi resep Senin–Minggu, potong saldo otomatis | Jadwal |
| Dompet | Set limit budget, catat pengeluaran, peringatan saldo rendah | Dompet |
| Daftar Belanja | Checklist belanja warung + estimasi harga | Belanja |
| Ekspor PDF | Export resep / daftar belanja ke PDF A4 | Menu, Belanja |
| NeoToast | Notifikasi aksi (tambah, hapus, simpan) gaya neo-brutalist | Global |
| Onboarding API Key | Popup pertama kali buka app mengarah ke Settings | Global |

---

## Navigasi Aplikasi

| Tab | Route | File UI |
|-----|-------|---------|
| MENU | `dashboard` | `DashboardScreen.kt` |
| JADWAL | `planner` | `PlannerScreen.kt` |
| DOMPET | `budget` | `BudgetScreen.kt` |
| BELANJA | `grocery` | `GroceryScreen.kt` |

Header global: logo MASAKIN + tombol Settings (kuning) untuk API Key.

---

## Tech Stack

| Kategori | Library / Tool | Versi (referensi) |
|----------|----------------|-------------------|
| Build | Android Gradle Plugin | 8.7.3 |
| Language | Kotlin | 2.1.0 |
| UI | Compose BOM | 2024.11.00 |
| Navigation | Navigation Compose | 2.8.4 |
| Database | Room | 2.6.1 |
| Preferences | DataStore Preferences | 1.1.1 |
| HTTP | Retrofit + Moshi | 2.11.0 / 1.15.1 |
| Splash | Core Splashscreen | 1.0.1 |
| Compile SDK | Android 35 | — |
| Min SDK | 24 | — |
| Target SDK | 34 | — |

---

## Palet Warna (Neo-Brutalism)

| Token | Hex | Penggunaan |
|-------|-----|------------|
| NeoYellow | `#FFDE4D` | Tab aktif, tombol Settings |
| NeoPink | `#FF2E93` | Wishlist, toast hapus |
| NeoGreen | `#00FF66` | Sukses, toast berhasil |
| NeoCyan | `#00F0FF` | Info, deteksi model |
| NeoPurple | `#7C3AED` | Banner branding |
| NeoBlack | `#111111` | Border, shadow, teks |
| NeoWhite | `#FFFFFF` | Kartu, header |
| Background | `#EBF3FC` | Latar polka-dot |

---

## Persyaratan Pengembangan

| Komponen | Versi disarankan |
|----------|------------------|
| Android Studio | Ladybug (2024.2.1) atau lebih baru |
| JDK | 17 atau 21 (Gradle JVM) |
| Android SDK | API 35 (compile) |
| Git | Opsional |

---

## Instalasi dan Menjalankan

### 1. Clone proyek

```bash
git clone https://github.com/caidenrev/kost-chef-android.git
cd kost-chef-android
```

### 2. `local.properties`

Buat file `local.properties` di root proyek:

```properties
sdk.dir=C\:\\Users\\NamaUser\\AppData\\Local\\Android\\Sdk
```

### 3. Sync Gradle

Buka folder di Android Studio, lalu **File > Sync Project with Gradle Files**.

### 4. Jalankan di emulator / perangkat

```powershell
# Windows
.\gradlew.bat installDebug
```

```bash
# macOS / Linux
./gradlew installDebug
```

### 5. API Key Gemini (wajib untuk AI)

1. Dapatkan key di [Google AI Studio](https://aistudio.google.com/)
2. Buka app > tombol **Settings** (ikon gerigi kuning di header)
3. Masukkan API Key > **SIMPAN**

Pada instalasi pertama, popup selamat datang akan mengarahkan ke langkah ini.

---

## Build Release (Play Store)

| Langkah | Perintah / File |
|---------|-----------------|
| Keystore | `masakin-release.jks` (buat dengan `keytool`) |
| Konfigurasi | `keystore.properties` (jangan di-commit) |
| Bundle AAB | `.\gradlew.bat bundleRelease` |
| Output | `app/build/outputs/bundle/release/app-release.aab` |

Detail lengkap: [docs/PLAYSTORE.md](docs/PLAYSTORE.md)

---

## Struktur Proyek

```
chefairevan/
├── MASAKIN.png              # Logo resmi
├── app/
│   └── src/main/
│       ├── java/.../chef_ai_revan/
│       │   ├── data/        # Room, DAO, Repository, API
│       │   ├── viewmodel/   # BudgetViewModel
│       │   ├── ui/
│       │   │   ├── screens/     # Dashboard, Planner, Budget, Grocery
│       │   │   ├── components/  # NeoToast, dialogs, PDF, logo
│       │   │   ├── navigation/
│       │   │   └── theme/
│       │   └── MainActivity.kt
│       └── res/
├── docs/                    # Dokumentasi tambahan
├── gradle/
└── README.md
```

---

## Dokumentasi Lanjutan

| Dokumen | Isi |
|---------|-----|
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Arsitektur MVVM, alur data, komponen UI |
| [docs/DATABASE.md](docs/DATABASE.md) | Skema Room, tabel, DataStore |
| [docs/SETUP.md](docs/SETUP.md) | Setup dev, signing, troubleshooting |
| [docs/API.md](docs/API.md) | Integrasi Gemini API dan fallback cloud |
| [docs/PLAYSTORE.md](docs/PLAYSTORE.md) | Checklist publikasi Play Store |

---

## Privasi dan Data

| Data | Lokasi | Dikirim ke internet? |
|------|--------|----------------------|
| Budget, jadwal, belanja, wishlist | SQLite di perangkat | Tidak |
| Gemini API Key | DataStore lokal | Ya (hanya ke Google saat generate resep) |
| Prompt resep | — | Ya (ke Gemini API) |

Tidak ada sinkronisasi cloud akun. Uninstall app = data ikut terhapus.

---

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Gradle sync gagal | Cek `local.properties`, JDK, koneksi internet |
| AI tidak jalan | Isi API Key di Settings; cek kuota Gemini |
| Password keystore salah | Verifikasi dengan `keytool -list -keystore masakin-release.jks` |
| Tombol Run abu-abu | Sync Gradle; Run Configuration modul `app` |
| Database reset | Normal saat dev (`fallbackToDestructiveMigration`) |

---

## Lisensi dan Kredit

Proyek ini dikembangkan oleh **Revan** sebagai **MASAKIN**.

Untuk pertanyaan teknis, lihat folder [docs/](docs/) atau buka issue di repository.
