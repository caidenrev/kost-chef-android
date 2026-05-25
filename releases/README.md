# Paket APK MASAKIN

| File | Versi | Keterangan |
|------|-------|------------|
| [MASAKIN-v1.2.apk](MASAKIN-v1.2.apk) | 1.2 | **Pakai ini** — perbaikan instal "App not installed" |
| [MASAKIN-v1.1.apk](MASAKIN-v1.1.apk) | 1.1 | Arsip |
| [MASAKIN-v1.0.apk](MASAKIN-v1.0.apk) | 1.0 | Arsip |

## Unduh dari README

Link unduh di [README utama](../README.md#unduh-apk):

`https://github.com/caidenrev/kost-chef-android/raw/main/releases/MASAKIN-v1.2.apk`

## Memperbarui APK (maintainer)

Setelah ubah kode, generate APK baru:

```powershell
.\gradlew.bat assembleRelease
Copy-Item app\build\outputs\apk\release\app-release.apk releases\MASAKIN-v1.2.apk
```

Lalu commit, push, dan opsional buat [GitHub Release](https://github.com/caidenrev/kost-chef-android/releases) dengan lampiran APK yang sama.
