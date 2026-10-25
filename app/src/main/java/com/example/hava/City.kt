package com.example.hava

// API'den gelen tek bir şehir.
// Alan adları JSON'daki anahtarlarla birebir aynı olmalı ki Gson eşleştirebilsin.
data class City(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?, // bazı sonuçlarda gelmeyebilir → ? (nullolabilir)
    val admin1: String?,
    val temperature: Double? = null,   // hava durumu API'sinden sonradan dolduruluyor (Repository'de)
    val weatherCode: Int? = null
)

// API'nin döndürdüğü cevabın tamamı
data class CitySearchResponse(
    val results: List<City>?   // sonuç yoksa API bu alanı hiç göndermiyor → null
)

// Arama kutusu boşken gösterilecek büyük şehirler (koordinatlar sabit, arama API'sine gerek yok)
val defaultCities = listOf(
    City(id = 1, name = "İstanbul", latitude = 41.01, longitude = 28.98, country = "Türkiye", admin1 = "İstanbul"),
    City(id = 2, name = "Ankara", latitude = 39.92, longitude = 32.85, country = "Türkiye", admin1 = "Ankara"),
    City(id = 3, name = "İzmir", latitude = 38.42, longitude = 27.14, country = "Türkiye", admin1 = "İzmir"),
    City(id = 4, name = "Bursa", latitude = 40.19, longitude = 29.06, country = "Türkiye", admin1 = "Bursa"),
    City(id = 5, name = "Antalya", latitude = 36.90, longitude = 30.70, country = "Türkiye", admin1 = "Antalya"),
    City(id = 6, name = "Adana", latitude = 37.00, longitude = 35.32, country = "Türkiye", admin1 = "Adana"),
    City(id = 7, name = "Konya", latitude = 37.87, longitude = 32.48, country = "Türkiye", admin1 = "Konya"),
    City(id = 8, name = "Gaziantep", latitude = 37.07, longitude = 37.38, country = "Türkiye", admin1 = "Gaziantep"),
    City(id = 9, name = "Şanlıurfa", latitude = 37.16, longitude = 38.79, country = "Türkiye", admin1 = "Şanlıurfa"),
    City(id = 10, name = "Kocaeli", latitude = 40.77, longitude = 29.94, country = "Türkiye", admin1 = "Kocaeli")
)
