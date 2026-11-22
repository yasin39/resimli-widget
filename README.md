# Resimli Widget

Android ana ekran widget'ı: Allah'ın isimleri ve "Kuran İle Hayat Nasıl Yaşanır" başlıkları, resim üzerinde, rastgele sırayla.

- Her 30 dakikada bir değişir, dokununca hemen yenisi gelir.
- Kotlin + Jetpack Glance + WorkManager, minSdk 26.

## Derleme
1. Projeyi Android Studio ile açın, Gradle senkronizasyonunu bekleyin.
2. Uygulamanın ayrı bir ekranı yok. Run > Edit Configurations > Launch: **Nothing** seçip çalıştırın.
3. Ana ekranda boş bir yere basılı tutun > Widget'lar > **Resimli Widget**.

## Veri
`app/src/main/assets/` altında `names.json`, `topics.json` ve görseller (`names/`, `topics/`) bulunur. Görseli olmayan kayıtlarda yedek arka plan kullanılır.
