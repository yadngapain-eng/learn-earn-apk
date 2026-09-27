# Learn Earn - APK Builder

Build APK otomatis dari website https://duniamu.my.id
menggunakan Trusted Web Activity (TWA) + GitHub Actions.

## Info APK

| Item | Value |
|------|-------|
| App Name | Learn Earn |
| Package | learn.earn.com |
| Website | https://duniamu.my.id |
| Version | 1.0.0 (1) |
| Min Android | 5.0 (API 21) |
| Target Android | 14 (API 34) |

## Cara Download APK

1. Buka tab Actions di repo ini
2. Klik workflow terakhir yang sukses
3. Scroll ke bawah ke bagian Artifacts
4. Download:
   - learn-earn-debug-apk (untuk testing)
   - learn-earn-release-apk (untuk distribusi)

## Cara Build Manual

Klik Actions - Build APK - Run workflow - Run workflow

## Install APK di HP

1. Download APK dari GitHub Actions
2. Transfer ke HP
3. Buka file APK - Install
4. Buka app Learn Earn dari homescreen

## Sign APK untuk Play Store

APK hasil build ini unsigned. Untuk upload ke Play Store,
perlu signing key. Tambahkan secrets:
- KEYSTORE_BASE64
- KEYSTORE_PASSWORD
- KEY_ALIAS
- KEY_PASSWORD

## Verifikasi Domain (TWA)

Untuk TWA bekerja sempurna (tanpa address bar), perlu:
1. File .well-known/assetlinks.json di website
2. SHA256 fingerprint dari keystore

## License

MIT - 2026
