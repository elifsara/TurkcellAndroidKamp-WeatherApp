# 🌤️ Hava Durumu

Turkcell Bilişim Kampı (Android) için geliştirilmiş, Jetpack Compose ile yazılmış bir hava durumu uygulaması.
Şehir arayabilir, favorilerini kaydedebilir, bulunduğun yerin ve herhangi bir şehrin saatlik/günlük tahminini görebilirsin.

| Ana ekran | Arama | Şehir detayı |
|:-:|:-:|:-:|
| <img src="docs/screenshots/main.png" width="220"> | <img src="docs/screenshots/search.png" width="220"> | <img src="docs/screenshots/detail.png" width="220"> |

## Özellikler

- **Yazdıkça arama**: 400 ms debounce ile; 1–2 harfte 81 il içinde (internetsiz), 3+ harfte tüm dünyada arar
- **Gerçek hava durumu**: Kartlar havaya ve gece/gündüze göre renk değiştirir
- **Tembel yükleme**: Bir kartın sıcaklığı ancak kart ekrana gelince istenir (LazyColumn + `LaunchedEffect`)
- **Şehir detayı**: 24 saatlik tahmin, dün/bugün/yarın kartları, önümüzdeki günler, nem/rüzgâr/hissedilen/gün doğumu
- **Favoriler**: Room veritabanında saklanır, uygulama kapanınca kaybolmaz
- **Konumum**: Çalışma anında konum izni istenir, reddedilirse Ayarlar'a yönlendirilir
- **Aşağı çekip yenileme**, iskelet (skeleton) yükleme animasyonu, hata ve "sonuç yok" ekranları
- **Açık / koyu tema**, kendi uygulama ikonu

## Kullanılan teknolojiler

| Konu | Kullanılan |
|---|---|
| Arayüz | Jetpack Compose, Material 3 |
| Mimari | MVVM + Repository |
| Ağ | Retrofit, Gson, OkHttp Logging Interceptor |
| Asenkron | Kotlin Coroutines, Flow |
| Veritabanı | Room (KSP) |
| Konum | LocationManager, Geocoder, Activity Result API (izinler) |
| Test | JUnit, kotlinx-coroutines-test, sahte (fake) sınıflar |
| Veri | [Open-Meteo](https://open-meteo.com/) (ücretsiz, API anahtarı gerektirmez) |

## Mimari

```mermaid
flowchart LR
    UI["Ekran<br/>(MainActivity, DetailActivity)"] --> VM["ViewModel<br/>(CityViewModel, DetailViewModel)"]
    VM --> R["CityRepository"]
    R --> API["Open-Meteo API<br/>(Retrofit)"]
    R --> DB["Room veritabanı<br/>(favoriler)"]
    R --> LOC["Konum<br/>(LocationProvider)"]
    R --> LIST["81 il listesi<br/>(koda gömülü)"]
```

- **Ekran** sadece gösterir ve kullanıcının dokunuşlarını ViewModel'e iletir.
- **ViewModel** ekranın durumunu (`Loading / Success / Empty / Error`) tutar; ekran döndürülünce ölmez.
- **Repository** verinin nereden geleceğine karar verir. ViewModel internetten mi veritabanından mı geldiğini bilmez.
  Bu sayede testlerde gerçek API ve veritabanı yerine sahte sınıflar verilebiliyor.

## Yaşam döngüsü (Logcat)

Activity ve ViewModel'lerin doğup ölmesi Logcat'e yazılır. Android Studio → **Logcat** → `tag:Yasam`:

1. Bir şehre dokun: `DetailActivity #1 onCreate`, `DetailViewModel #1 oluşturuldu`, `API isteği #1`.
   Arkadaki MainActivity ölmez, sadece `onStop` olur.
2. Telefonu yan çevir: `DetailActivity #1 onDestroy (döndürme)` → `DetailActivity #2 onCreate`.
   **Yeni ViewModel oluşmaz ve API'ye tekrar istek gitmez**: ViewModel döndürmeden etkilenmez.
3. Geri tuşu: `DetailViewModel #1 silindi (onCleared)` ve `DetailActivity #2 onDestroy (kapandı)`.

## Çalıştırma

1. Projeyi Android Studio ile aç, Gradle senkronizasyonunun bitmesini bekle.
2. Bir emülatör ya da telefon seçip ▶️ Run'a bas (minSdk 24).

Testleri çalıştırmak için:

```bash
./gradlew testDebugUnitTest
```

## Proje yapısı

```
app/src/main/java/com/example/hava/
├── MainActivity.kt        Ana ekran: başlık, arama, konum kartı, favoriler, şehir listesi
├── DetailActivity.kt      Şehir detayı için ayrı Activity
├── DetailScreen.kt        Detay ekranının arayüzü
├── CityViewModel.kt       Ana ekranın durumu (arama, favoriler, konum, yenileme)
├── DetailViewModel.kt     Detay ekranının durumu
├── CityRepository.kt      API, veritabanı ve konumu tek yerden yöneten katman
├── CityApi.kt / WeatherApi.kt / ApiClient.kt   Retrofit
├── AppDatabase.kt / FavoriteDao.kt / FavoriteCity.kt   Room
├── LocationProvider.kt    Telefonun konumu → şehir
├── LifecycleLog.kt        Activity/ViewModel olaylarını Logcat'e yazar
└── ...                    Kartlar, durum ekranları, tema, hava kodları
```
