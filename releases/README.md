# Paket APK MASAKIN

| File | Versi | Keterangan |
|------|-------|------------|
| [MASAKIN-v1.0.apk](MASAKIN-v1.0.apk) | 1.0 | Build debug, siap instal |

## Unduh dari README

Link unduh di [README utama](../README.md#unduh-apk):

`https://github.com/caidenrev/kost-chef-android/raw/main/releases/MASAKIN-v1.0.apk`

## Memperbarui APK (maintainer)

Setelah ubah kode, generate APK baru:

```powershell
.\gradlew.bat assembleDebug
Copy-Item app\build\outputs\apk\debug\app-debug.apk releases\MASAKIN-v1.1.apk
```

Lalu commit, push, dan opsional buat [GitHub Release](https://github.com/caidenrev/kost-chef-android/releases) dengan lampiran APK yang sama.
