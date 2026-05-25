# Panduan Publikasi Google Play Store

<p align="center">
  <img src="../MASAKIN.png" alt="MASAKIN" width="200">
</p>

## Checklist Sebelum Upload

| No | Item | Status |
|----|------|--------|
| 1 | Akun Google Play Developer ($25 sekali) | |
| 2 | `applicationId` final (tidak `com.example.*`) | |
| 3 | `targetSdk` memenuhi syarat Play terbaru | |
| 4 | Keystore release valid + password tersimpan | |
| 5 | AAB signed: `bundleRelease` | |
| 6 | Privacy Policy URL publik | |
| 7 | Screenshot minimal 2 | |
| 8 | Feature graphic 1024x500 | |
| 9 | Icon 512x512 (dari MASAKIN.png) | |
| 10 | Deskripsi store (ID/EN) | |

## Build AAB Release

```powershell
.\gradlew.bat bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

## Informasi App (contoh)

| Field | Nilai saran |
|-------|-------------|
| Nama | MASAKIN |
| Kategori | Food & Drink |
| Gratis | Ya |
| Konten | Semua umur (sesuaikan kuesioner) |
| Iklan | Tidak (jika tidak ada AdMob) |

## Privacy Policy (poin wajib)

Jelaskan di halaman web:

- Data disimpan lokal di perangkat (budget, jadwal, belanja, wishlist)
- API Key Gemini disimpan lokal, dikirim ke Google saat fitur AI dipakai
- Tidak ada login / cloud sync akun
- Uninstall menghapus data lokal

## Data Safety Form (Play Console)

| Pertanyaan | Jawaban umum |
|------------|--------------|
| Koleksi data? | Ya (API usage via user key) |
| Data dienkripsi transit? | Ya (HTTPS) |
| Bisa hapus data? | Ya (uninstall / clear storage) |
| Dibagikan ke pihak ketiga? | Google (Gemini) saat user pakai AI |

## Alur Rilis

```
Internal testing  -->  Closed testing (opsional)  -->  Production
```

1. Play Console > Create app
2. Store listing + aset grafis
3. App content (rating, data safety, target audience)
4. Release > Internal testing > upload AAB
5. Tambah tester email
6. Setelah stabil > Production > Submit for review

## Keystore Hilang atau Password Salah

| Situasi | Dampak |
|---------|--------|
| Password salah | Tidak bisa sign AAB; perbaiki password atau buat keystore baru |
| Keystore hilang, app belum publish | Buat keystore baru, tidak masalah |
| Keystore hilang, app sudah publish | **Tidak bisa update** app yang sama; hubungi Google Play support |

## Versi Berikutnya

Setiap update naikkan di `app/build.gradle.kts`:

| Field | Aturan |
|-------|--------|
| versionCode | Integer, harus lebih besar dari sebelumnya |
| versionName | String tampilan user (mis. 1.1) |

```kotlin
versionCode = 2
versionName = "1.1"
```
