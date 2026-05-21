# Panduan Keamanan & Pengelolaan API Key di Android 🛡️

Dokumen ini menjelaskan praktik terbaik untuk mengelola API Key, rahasia (*secrets*), dan variabel lingkungan (.env) pada aplikasi Android Native **MASAKIN by Revan**.

---

## 方案 1: Serverless API Proxy via Vercel (Rekomendasi Produksi) 🌟

Ini adalah solusi **paling aman** karena menyembunyikan API Key berharga Anda (seperti Gemini API Key) sepenuhnya di sisi server.

### Cara Kerja:
```
[HP Android] ---> (HTTP Request) ---> [Vercel Serverless Function] ---> [Gemini API Server]
                                              | (Memakai Key Aman)
[HP Android] <--- (Resep JSON)   <--- [Vercel Serverless Function]
```

### Langkah Implementasi di Vercel:
1. Buat project baru di Vercel berbasis Node.js/Next.js.
2. Di Dashboard Vercel, masuk ke **Settings -> Environment Variables** dan tambahkan `GEMINI_API_KEY`.
3. Buat file API Route `/api/generate-recipe.js`:
   ```javascript
   // api/generate-recipe.js
   import { GoogleGenAI } from '@google/generative-ai';

   export default async function handler(req, res) {
       const { ingredients, budget } = req.body;
       const genAI = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });
       const model = genAI.getGenerativeModel({ model: "gemini-pro" });
       
       const prompt = `Buatkan resep berdasarkan bahan: ${ingredients} dengan budget Rp ${budget}`;
       const result = await model.generateContent(prompt);
       const response = await result.response;
       
       res.status(200).json({ recipe: response.text() });
   }
   ```
4. Di aplikasi Android, gunakan library **Retrofit** atau **Ktor** untuk memanggil `https://project-anda.vercel.app/api/generate-recipe`.

---

## 方案 2: Secrets Gradle Plugin (Rekomendasi Uji Coba Lokal) 🛠️

Jika Anda ingin HP Android memanggil API secara langsung, gunakan plugin resmi Google ini agar API Key dibaca dari file lokal komputer Anda dan tidak terunggah ke repositori GitHub publik.

### Langkah-langkah Setup:

### 1. Tambahkan Plugin di root `build.gradle.kts`
```kotlin
plugins {
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}
```

### 2. Terapkan Plugin di `app/build.gradle.kts`
```kotlin
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    buildFeatures {
        buildConfig = true
    }
}
```

### 3. Simpan Kunci di `local.properties`
Di folder utama proyek Android Anda, buka file `local.properties` (file ini otomatis diabaikan oleh Git via `.gitignore`) dan masukkan API Key:
```properties
GEMINI_API_KEY=AIzaSyYourActualAPIKeyHere
```

### 4. Panggil Kunci di Kode Kotlin
Setelah proses build ulang proyek (*Clean & Rebuild*), plugin akan membuat kelas `BuildConfig` otomatis. Panggil kuncinya di kode Anda seperti ini:
```kotlin
val geminiKey = BuildConfig.GEMINI_API_KEY
// Gunakan geminiKey untuk memanggil Google Gen AI SDK
```

---

## 方案 3: Pengamanan Firebase Firestore 🔐

Berbeda dengan API Key biasa, API Key Firebase (seperti `google-services.json` atau `FIRESTORE_API_KEY`) **aman untuk dibiarkan terbuka di dalam APK**. Hal ini karena sistem Firebase mengamankan data di server-side menggunakan **Firebase Security Rules**.

### Contoh Aturan Keamanan Firestore (Hanya User Login yang Bisa Akses):
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /weekly_plans/{planId} {
      allow read, write: if request.auth != null;
    }
  }
}
```
Pastikan Anda selalu menyetel rules ini ke mode produksi di Konsol Firebase Anda sebelum mempublikasikan aplikasi ke Google Play Store!
