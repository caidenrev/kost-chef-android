# Integrasi API

<p align="center">
  <img src="../MASAKIN.png" alt="MASAKIN" width="200">
</p>

## Ringkasan

| Layanan | Fungsi | Wajib? |
|---------|--------|--------|
| Google Gemini API | Generate resep AI | Ya (user supply API Key) |
| Vercel backend | Fallback jika Gemini gagal | Otomatis (server developer) |

## Google Gemini API

### Mendapatkan API Key

1. Buka [Google AI Studio](https://aistudio.google.com/)
2. Buat API Key
3. Masukkan di app: **Settings (header) > SIMPAN**

### Penyimpanan Key

| Lokasi | Metode |
|--------|--------|
| Perangkat user | DataStore (`user_gemini_api_key`) |
| Transmisi | HTTPS ke `generativelanguage.googleapis.com` |

Key tidak disimpan di server MASAKIN.

### Deteksi Model

Setelah key disimpan, app memanggil `ListModels` dan memfilter model yang mendukung `generateContent`. Urutan percobaan mengikuti daftar preferensi di `GeminiClient.PREFERRED_MODELS`.

### Format Prompt

ViewModel mengirim prompt bahasa Indonesia berisi:

- Bahan terpilih user
- Limit budget (IDR)
- Instruksi output JSON (nama, cost, description, ingredients, steps)

### Parsing Respons

Respons teks dibersihkan dari markdown code fence, lalu diparse sebagai JSON ke `GeneratedRecipeMock`.

## Fallback Vercel Cloud

| Properti | Nilai |
|----------|-------|
| Base URL | `https://chef-ai-backend-blush.vercel.app/` |
| Dipanggil | Jika semua model Gemini gagal |
| Data dikirim | Bahan + budget limit |

Endpoint didefinisikan di `ApiService.kt` (`ChefAiApi`).

## File Terkait

| File | Peran |
|------|------|
| `data/api/ApiService.kt` | Retrofit Gemini + Vercel |
| `viewmodel/BudgetViewModel.kt` | Logika generate, retry model |
| `ui/components/ApiKeySettingsDialog.kt` | UI input key |
| `data/repository/BudgetRepository.kt` | Simpan/baca key DataStore |

## Izin Android

| Permission | Alasan |
|------------|--------|
| `INTERNET` | Panggilan API Gemini dan fallback |

## Privasi (untuk Play Store Data Safety)

| Data | Koleksi | Transfer | Tujuan |
|------|---------|----------|--------|
| API Key | User input, lokal | Ke Google saat generate | Fitur AI |
| Prompt resep | Generated di app | Ke Google / Vercel | Generate resep |
| Budget, jadwal, belanja | Lokal saja | Tidak | Fitur inti app |
