# Panduan Setup Pengembangan

<p align="center">
  <img src="../MASAKIN.png" alt="MASAKIN" width="200">
</p>

## Prasyarat

| Software | Versi minimum |
|----------|---------------|
| Android Studio | 2024.2.1 |
| JDK | 17 |
| Android SDK Platform | API 35 |
| Android SDK Build-Tools | Sesuai AGP 8.7 |

## Langkah Instalasi

### 1. Clone dan buka proyek

```bash
git clone <repository-url> chefairevan
```

Android Studio: **File > Open** > pilih folder `chefairevan`.

### 2. File `local.properties`

```properties
sdk.dir=C\:\\Users\\<USER>\\AppData\\Local\\Android\\Sdk
```

### 3. Gradle Sync

**File > Sync Project with Gradle Files**

Atau terminal:

```powershell
.\gradlew.bat tasks
```

### 4. Emulator atau perangkat fisik

| Opsi | Keterangan |
|------|------------|
| AVD | Pixel 6 Pro, API 34+ disarankan |
| HP fisik | Aktifkan USB Debugging |

```powershell
.\gradlew.bat installDebug
```

## Konfigurasi API Key

API Key **tidak** disimpan di `.env` saat runtime. Pengguna memasukkan lewat UI.

Untuk development, setelah app terbuka:

1. Tap tombol Settings (kuning) di header
2. Paste Gemini API Key dari [aistudio.google.com](https://aistudio.google.com/)
3. Tap SIMPAN

## Build Variant

| Variant | Perintah | Output |
|---------|----------|--------|
| Debug APK | `.\gradlew.bat assembleDebug` | `app/build/outputs/apk/debug/` |
| Release AAB | `.\gradlew.bat bundleRelease` | `app/build/outputs/bundle/release/` |
| Release APK | `.\gradlew.bat assembleRelease` | `app/build/outputs/apk/release/` |

## Signing Release

### Buat keystore (sekali)

```powershell
keytool -genkey -v -keystore masakin-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias masakin -storepass <PASSWORD> -keypass <PASSWORD> -dname "CN=Revan, OU=MASAKIN, O=Revan, L=Jakarta, ST=DKI, C=ID"
```

### File `keystore.properties` (root project)

```properties
storeFile=masakin-release.jks
storePassword=<PASSWORD>
keyAlias=masakin
keyPassword=<PASSWORD>
```

**Penting:** Jangan commit `keystore.properties` atau `*.jks` ke Git.

### Verifikasi keystore

```powershell
keytool -list -v -keystore masakin-release.jks -alias masakin
```

Jika password salah, keystore tidak bisa dipakai. Buat keystore baru jika password hilang (app Play Store baru diperlukan jika sudah pernah publish).

Gradle sudah dikonfigurasi di `app/build.gradle.kts` untuk membaca `keystore.properties` otomatis.

## Run Configuration Android Studio

| Field | Nilai |
|-------|-------|
| Name | MASAKIN |
| Module | `chef-ai-revan.app.main` atau `app` |
| Deploy | APK from app bundle atau default |

Jika tombol Run abu-abu: Sync Gradle, pilih modul `app`.

## Troubleshooting Build

| Error | Penyebab | Solusi |
|-------|----------|--------|
| SDK location not found | Tidak ada `local.properties` | Buat file dengan `sdk.dir` |
| Keystore password incorrect | Password salah di properties | Perbaiki password atau buat keystore baru |
| Failed to read key from keystore.properties | Path storeFile salah | `storeFile` harus `masakin-release.jks`, bukan `keystore.properties` |
| KSP version mismatch | Kotlin vs KSP tidak cocok | Samakan versi di `libs.versions.toml` |

## File yang Jangan Di-commit

| File | Alasan |
|------|--------|
| `local.properties` | Path SDK lokal |
| `keystore.properties` | Password signing |
| `*.jks` | Private key |
| `.env` | Rahasia (jika dipakai) |
| `app/build/` | Artefak build |
