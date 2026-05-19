# MASAKIN by Revan 🍳

**MASAKIN by Revan** adalah asisten cerdas berbasis Android Native (Jetpack Compose & Kotlin) yang dirancang khusus untuk mengelola pengeluaran makan harian dan perencanaan resep sehat dengan budget hemat secara efisien. 

Aplikasi ini mengusung antarmuka premium bertema **Neo-Brutalisme** modern dengan warna neon yang mencolok, tombol fisik beranimasi taktil, gaya huruf tebal presisi tinggi (*tracking-tighter*), serta latar belakang polkadot biru muda yang segar.

---

## 🌟 Fitur Utama

- **Rekomendasi Resep Harian & AI Generator**: Masukkan bahan belanjaan instan dan limit harga harian Anda, simulator AI Gemini akan mencarikan resep paling optimal lengkap dengan estimasi harga, bahan, dan cara memasak.
- **Perencanaan Makan Mingguan (7-Day Calendar Planner)**: Jadwalkan rencana makan dari Senin sampai Minggu secara presisi. Setiap makanan yang dijadwalkan akan memotong saldo aktif Anda secara real-time.
- **Rupiah Budget Tracker & Monitor (Dompet)**: Kelola pengeluaran harian dan bulanan Anda. Aplikasi dilengkapi dengan sistem alarm otomatis ketika pengeluaran menyentuh limit warning **80%** serta **Mode Tanggal Tua** yang aktif otomatis.
- **Daftar Belanja Checklist Warung**: Checklist interaktif untuk mencatat belanjaan Anda yang terintegrasi langsung dengan input estimasi harga yang simetris.
- **Ekspor Dokumen Cetak A4 PDF**: Ekspor detail resep masakan Anda atau daftar belanja warung Anda langsung ke dokumen PDF standard cetak A4 yang siap dibagikan atau diprint via Android Share Sheet.
- **Offline Room Database & Cloud Sync Toggle**: Data tersimpan aman di database lokal SQLite Room secara offline. Tersedia tombol sakelar (chip) Cloud Sync untuk menghubungkan dengan cloud server Firebase secara langsung.

---

## 🎨 Spesifikasi Desain (Neo-Brutalisme Premium)

Aplikasi ini dirancang dengan gaya **Neo-Brutalisme** tingkat tinggi dengan spesifikasi teknis sebagai berikut:

- **Palet Warna Utama**:
  - `NeoYellow` = `#FFDE4D` (Neon Kuning)
  - `NeoPink` = `#FF2E93` (Neon Pink)
  - `NeoGreen` = `#00FF66` (Neon Hijau Kontras)
  - `NeoCyan` = `#00F0FF` (Neon Cyan)
  - `NeoPurple` = `#7C3AED` (Neon Ungu)
  - `NeoBlack` = `#111111` (Batas & Bayangan Solid)
  - `NeoBackground` = `#EBF3FC` (Latar Belakang Biru Sangat Muda Pastel)
- **Motif Polka-Dot Canvas**:
  - Latar belakang digambar dinamis menggunakan Canvas (`drawCircle`) dengan bulatan titik ber-radius **`2.8.dp`**, jarak sebaran titik **`20.dp`**, dan berwarna **Biru Kontras** (`Color(0xFF2E86C1)`) dengan transparansi halus **`15%`**.
- **Taktil Fisik Animasi Tombol**:
  - Saat ditekan, tombol akan bergeser turun-kanan secara fisik sejauh `5.dp` sedangkan bayangannya menyusut menjadi `3.dp` (menciptakan ilusi tombol 3D yang benar-benar amblas ke dalam saat disentuh).
- **Tipografi Presisi Tinggi (Ala Inter Web)**:
  - Gaya huruf menggunakan SansSerif bawaan Android dengan penyesuaian khusus: Judul utama menggunakan **`FontWeight.Black` (Bobot Tebal 900)** dan **`letterSpacing` negatif (antara `-0.5.sp` hingga `-1.5.sp`)** untuk meniru efek spasi rapat (*tracking-tighter*) modern.

---

## 🛠️ Persyaratan Sistem (Sebelum Kloning)

Sebelum memindahkan/mengkloning aplikasi ini ke komputer atau perangkat lain, pastikan komputer tujuan memiliki spesifikasi berikut:

1. **Android Studio**: Android Studio Ladybug (2024.2.1) atau versi di atasnya.
2. **Java Development Kit (JDK)**: **JDK 17** (pilih Gradle JDK Runtime JDK 17 di setingan Android Studio).
3. **Gradle**: Versi 8.x atau yang lebih baru (Kotlin Gradle DSL `build.gradle.kts`).
4. **Android SDK**: Compile SDK `36` (atau 34/35) dan Minimum SDK `24` (Android 7.0 Nougat).
5. **KSP (Kotlin Symbol Processing)**: Sesuai dengan versi Kotlin yang digunakan.

---

## 🚀 Panduan Kloning & Jalankan di Komputer Lain

Ikuti langkah-langkah berikut secara berurutan untuk memindahkan dan menjalankan aplikasi di laptop/perangkat baru:

### Langkah 1: Kloning Repositori
Jalankan perintah Git clone pada terminal komputer baru Anda:
```bash
git clone https://github.com/username-anda/chefairevan.git
```
*(Atau salin seluruh direktori proyek `chefairevan` ke flashdisk lalu pindahkan ke komputer baru).*

### Langkah 2: Konfigurasi Environment & local.properties
Di direktori utama (root) proyek, buat file bernama **`local.properties`** jika belum ada. Isi dengan direktori path Android SDK komputer baru Anda:

**Contoh isi `local.properties` (Windows):**
```properties
sdk.dir=C\:\\Users\\NamaUserKomputerBaru\\AppData\\Local\\Android\\Sdk
```
**Contoh isi `local.properties` (macOS/Linux):**
```properties
sdk.dir=/Users/NamaUserKomputerBaru/Library/Android/sdk
```

### Langkah 3: Setup File Lingkungan (.env)
1. Lihat file template **`.env.example`** di direktori utama.
2. Buat file baru bernama **`.env`** (atau salin dari `.env.example`).
3. Masukkan API Key Google Gemini Anda dan konfigurasi Firebase Project ID milik Anda:
   ```env
   GEMINI_API_KEY=AIzaSyYourRealGeminiKeyHere
   FIRESTORE_PROJECT_ID=masakin-by-revan
   FIRESTORE_API_KEY=FirebaseWebApiKeyHere
   ```
4. Di Android Studio, variabel ini akan dibaca secara otomatis atau disimulasikan secara aman tanpa merusak struktur offline database.

### Langkah 4: Buka Proyek di Android Studio
1. Jalankan **Android Studio**.
2. Pilih **File -> Open** lalu pilih folder direktori proyek **`chefairevan`**.
3. Tunggu hingga proses **Gradle Sync** selesai (pastikan komputer terhubung ke internet saat sync pertama kali untuk mengunduh dependencies libraries).

### Langkah 5: Jalankan & Build Aplikasi
Anda dapat langsung menjalankan aplikasi ke emulator atau HP Android fisik:
1. Hubungkan HP Android asli Anda dengan mengaktifkan mode *USB Debugging*.
2. Klik tombol **Run (Segitiga Hijau)** di pojok kanan atas Android Studio.
3. Atau jalankan perintah kompilasi manual lewat terminal Android Studio:
   - **Windows PowerShell**:
     ```powershell
     .\gradlew.bat assembleDebug
     ```
   - **macOS / Linux Terminal**:
     ```bash
     ./gradlew assembleDebug
     ```
4. File APK hasil build akan berlokasi di: `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📦 Arsitektur Kode & Database Schema

Aplikasi ini menggunakan pola arsitektur **MVVM (Model-View-ViewModel)** dengan komponen utama:
- **`com.example.chef_ai_revan.data.entity`**:
  - `Budget`: Menyimpan limit mingguan dan sisa saldo aktif.
  - `FavoriteRecipe`: Menyimpan data resep masakan favorit (Wishlist).
  - `WeeklyPlan`: Menyimpan alokasi jadwal makan harian (Senin - Minggu).
  - `GroceryItem`: Menyimpan daftar belanjaan warung offline.
- **`com.example.chef_ai_revan.data.dao`**: Interface SQLite query Room Database.
- **`com.example.chef_ai_revan.viewmodel.BudgetViewModel`**: Penampung status state reaktif (StateFlow) dan jembatan logika UI ke repositori database.
- **`com.example.chef_ai_revan.ui.components.PdfExporter`**: Canvas Exporter yang menggambar file PDF secara programatik berukuran A4 dan mengirimkannya via FileProvider.

---

## 🔧 Pemecahan Masalah (Troubleshooting)

- **Masalah KSP / Gradle Version Mismatch**:
  Jika Anda mengganti versi Kotlin di proyek, pastikan Anda juga memperbarui versi plugin `google.devtools.ksp` di `gradle/libs.versions.toml` agar versinya cocok dengan Kotlin compiler.
- **Error Sharing PDF (FileProvider Exception)**:
  Aplikasi menggunakan FileProvider untuk membagikan PDF. Pastikan setingan XML path di `app/src/main/res/xml/file_paths.xml` dan deklarasi `<provider>` di `AndroidManifest.xml` tidak diubah demi keamanan akses storage.
- **Room Database Migration Error**:
  Saat pengembangan, jika Anda mengubah properti tabel di entities, aplikasi dikonfigurasi menggunakan `.fallbackToDestructiveMigration()` sehingga database lokal akan di-reset otomatis tanpa memicu crash skema di HP baru.

---

Dibuat dengan ❤️ oleh **MASAKIN by Revan** - Asisten Masak & Saldo Anti Tanggal Tua!
