# Database dan Penyimpanan Data

<p align="center">
  <img src="../MASAKIN.png" alt="MASAKIN" width="200">
</p>

Semua data pengguna disimpan **lokal di perangkat**. Tidak ada server database milik developer.

## Room Database

| Properti | Nilai |
|----------|-------|
| Nama file | `chef_ai_revan_db` |
| Versi skema | 3 |
| Lokasi | Internal storage app (`/data/data/<package>/databases/`) |
| Migrasi | `fallbackToDestructiveMigration()` (dev: data reset jika skema berubah) |

## Tabel `budget`

| Kolom | Tipe | Keterangan |
|-------|------|------------|
| id | INT (PK) | Selalu `1` (satu baris aktif) |
| limit | DOUBLE | Limit budget mingguan (IDR) |
| currentBalance | DOUBLE | Saldo tersisa |
| startDate | LONG | Timestamp mulai periode |

## Tabel `weekly_plans`

| Kolom | Tipe | Keterangan |
|-------|------|------------|
| dayIndex | INT (PK) | 0=Senin … 6=Minggu |
| dayName | TEXT | Nama hari (SENIN, …) |
| recipeName | TEXT? | Nama resep terjadwal |
| estimatedCost | DOUBLE | Biaya estimasi |
| description | TEXT? | Deskripsi singkat |
| ingredientsList | TEXT? | Format: `bahan:harga\|\|bahan2:harga2` |
| steps | TEXT? | Format: `langkah1\|\|langkah2` |

## Tabel `favorite_recipes` (Wishlist)

| Kolom | Tipe | Keterangan |
|-------|------|------------|
| id | INT (PK, auto) | ID unik |
| name | TEXT | Nama resep |
| estimatedCost | DOUBLE | Estimasi biaya |
| description | TEXT | Deskripsi |
| ingredientsList | TEXT | Bahan (format CSV custom) |

## Tabel `grocery_items`

| Kolom | Tipe | Keterangan |
|-------|------|------------|
| id | INT (PK, auto) | ID unik |
| name | TEXT | Nama barang |
| estimatedCost | DOUBLE | Harga estimasi |
| isChecked | BOOLEAN | Status checklist |

## DataStore Preferences

| Key | Tipe | Keterangan |
|-----|------|------------|
| `user_gemini_api_key` | String | API Key Gemini pengguna |
| `api_key_intro_shown` | Boolean | Onboarding API sudah ditampilkan |

File DataStore: `settings.preferences_pb` di folder data aplikasi.

## DAO dan Repository

| DAO | Entity | Operasi utama |
|-----|--------|---------------|
| BudgetDao | Budget | get, insert, update balance |
| WeeklyPlanDao | WeeklyPlan | get all, update day, clear |
| FavoriteRecipeDao | FavoriteRecipe | CRUD favorites |
| GroceryItemDao | GroceryItem | CRUD + toggle checked |

`BudgetRepository` menggabungkan semua DAO + DataStore menjadi satu pintu akses untuk ViewModel.

## Diagram Relasi (konseptual)

```
Budget (1) ── digunakan oleh ── semua layar dompet & jadwal
WeeklyPlan (7 baris) ── terhubung ke potongan saldo Budget
FavoriteRecipe (N) ── independen, dari AI Generator
GroceryItem (N) ── independen, layar Belanja
DataStore ── API Key + flag onboarding
```
