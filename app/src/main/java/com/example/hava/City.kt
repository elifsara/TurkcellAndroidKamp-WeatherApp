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
    val weatherCode: Int? = null,
    val isDay: Boolean? = null         // gündüz mü gece mi (kart rengi ve 🌙 için)
)

// API'nin döndürdüğü cevabın tamamı
data class CitySearchResponse(
    val results: List<City>?   // sonuç yoksa API bu alanı hiç göndermiyor → null
)

// Türkiye'nin 81 ili (id = plaka). Kısa aramalar bu listede yapılır, internet gerekmez.
val turkishProvinces = listOf(
    City(id = 1, name = "Adana", latitude = 36.99, longitude = 35.33, country = "Türkiye", admin1 = "Adana"),
    City(id = 2, name = "Adıyaman", latitude = 37.76, longitude = 38.28, country = "Türkiye", admin1 = "Adıyaman"),
    City(id = 3, name = "Afyonkarahisar", latitude = 38.76, longitude = 30.54, country = "Türkiye", admin1 = "Afyonkarahisar"),
    City(id = 4, name = "Ağrı", latitude = 39.71, longitude = 43.04, country = "Türkiye", admin1 = "Ağrı"),
    City(id = 5, name = "Amasya", latitude = 40.65, longitude = 35.83, country = "Türkiye", admin1 = "Amasya"),
    City(id = 6, name = "Ankara", latitude = 39.92, longitude = 32.85, country = "Türkiye", admin1 = "Ankara"),
    City(id = 7, name = "Antalya", latitude = 36.91, longitude = 30.7, country = "Türkiye", admin1 = "Antalya"),
    City(id = 8, name = "Artvin", latitude = 41.18, longitude = 41.82, country = "Türkiye", admin1 = "Artvin"),
    City(id = 9, name = "Aydın", latitude = 37.85, longitude = 27.84, country = "Türkiye", admin1 = "Aydın"),
    City(id = 10, name = "Balıkesir", latitude = 39.65, longitude = 27.89, country = "Türkiye", admin1 = "Balıkesir"),
    City(id = 11, name = "Bilecik", latitude = 40.14, longitude = 29.98, country = "Türkiye", admin1 = "Bilecik"),
    City(id = 12, name = "Bingöl", latitude = 38.88, longitude = 40.49, country = "Türkiye", admin1 = "Bingöl"),
    City(id = 13, name = "Bitlis", latitude = 38.4, longitude = 42.11, country = "Türkiye", admin1 = "Bitlis"),
    City(id = 14, name = "Bolu", latitude = 40.74, longitude = 31.61, country = "Türkiye", admin1 = "Bolu"),
    City(id = 15, name = "Burdur", latitude = 37.72, longitude = 30.29, country = "Türkiye", admin1 = "Burdur"),
    City(id = 16, name = "Bursa", latitude = 40.2, longitude = 29.06, country = "Türkiye", admin1 = "Bursa"),
    City(id = 17, name = "Çanakkale", latitude = 40.16, longitude = 26.41, country = "Türkiye", admin1 = "Çanakkale"),
    City(id = 18, name = "Çankırı", latitude = 40.6, longitude = 33.62, country = "Türkiye", admin1 = "Çankırı"),
    City(id = 19, name = "Çorum", latitude = 40.55, longitude = 34.95, country = "Türkiye", admin1 = "Çorum"),
    City(id = 20, name = "Denizli", latitude = 37.77, longitude = 29.09, country = "Türkiye", admin1 = "Denizli"),
    City(id = 21, name = "Diyarbakır", latitude = 37.91, longitude = 40.22, country = "Türkiye", admin1 = "Diyarbakır"),
    City(id = 22, name = "Edirne", latitude = 41.68, longitude = 26.56, country = "Türkiye", admin1 = "Edirne"),
    City(id = 23, name = "Elazığ", latitude = 38.67, longitude = 39.22, country = "Türkiye", admin1 = "Elazığ"),
    City(id = 24, name = "Erzincan", latitude = 39.74, longitude = 39.49, country = "Türkiye", admin1 = "Erzincan"),
    City(id = 25, name = "Erzurum", latitude = 39.91, longitude = 41.28, country = "Türkiye", admin1 = "Erzurum"),
    City(id = 26, name = "Eskişehir", latitude = 39.78, longitude = 30.52, country = "Türkiye", admin1 = "Eskişehir"),
    City(id = 27, name = "Gaziantep", latitude = 37.06, longitude = 37.38, country = "Türkiye", admin1 = "Gaziantep"),
    City(id = 28, name = "Giresun", latitude = 40.92, longitude = 38.39, country = "Türkiye", admin1 = "Giresun"),
    City(id = 29, name = "Gümüşhane", latitude = 40.46, longitude = 39.47, country = "Türkiye", admin1 = "Gümüşhane"),
    City(id = 30, name = "Hakkari", latitude = 37.57, longitude = 43.74, country = "Türkiye", admin1 = "Hakkari"),
    City(id = 31, name = "Hatay", latitude = 36.21, longitude = 36.16, country = "Türkiye", admin1 = "Hatay"),
    City(id = 32, name = "Isparta", latitude = 37.76, longitude = 30.55, country = "Türkiye", admin1 = "Isparta"),
    City(id = 33, name = "Mersin", latitude = 36.81, longitude = 34.64, country = "Türkiye", admin1 = "Mersin"),
    City(id = 34, name = "İstanbul", latitude = 41.01, longitude = 28.95, country = "Türkiye", admin1 = "İstanbul"),
    City(id = 35, name = "İzmir", latitude = 38.41, longitude = 27.14, country = "Türkiye", admin1 = "İzmir"),
    City(id = 36, name = "Kars", latitude = 40.6, longitude = 43.09, country = "Türkiye", admin1 = "Kars"),
    City(id = 37, name = "Kastamonu", latitude = 41.38, longitude = 33.78, country = "Türkiye", admin1 = "Kastamonu"),
    City(id = 38, name = "Kayseri", latitude = 38.73, longitude = 35.49, country = "Türkiye", admin1 = "Kayseri"),
    City(id = 39, name = "Kırklareli", latitude = 41.74, longitude = 27.23, country = "Türkiye", admin1 = "Kırklareli"),
    City(id = 40, name = "Kırşehir", latitude = 39.15, longitude = 34.16, country = "Türkiye", admin1 = "Kırşehir"),
    City(id = 41, name = "Kocaeli", latitude = 40.76, longitude = 29.93, country = "Türkiye", admin1 = "Kocaeli"),
    City(id = 42, name = "Konya", latitude = 37.87, longitude = 32.48, country = "Türkiye", admin1 = "Konya"),
    City(id = 43, name = "Kütahya", latitude = 39.42, longitude = 29.98, country = "Türkiye", admin1 = "Kütahya"),
    City(id = 44, name = "Malatya", latitude = 38.35, longitude = 38.32, country = "Türkiye", admin1 = "Malatya"),
    City(id = 45, name = "Manisa", latitude = 38.61, longitude = 27.43, country = "Türkiye", admin1 = "Manisa"),
    City(id = 46, name = "Kahramanmaraş", latitude = 37.58, longitude = 36.93, country = "Türkiye", admin1 = "Kahramanmaraş"),
    City(id = 47, name = "Mardin", latitude = 37.31, longitude = 40.74, country = "Türkiye", admin1 = "Mardin"),
    City(id = 48, name = "Muğla", latitude = 37.22, longitude = 28.37, country = "Türkiye", admin1 = "Muğla"),
    City(id = 49, name = "Muş", latitude = 38.73, longitude = 41.48, country = "Türkiye", admin1 = "Muş"),
    City(id = 50, name = "Nevşehir", latitude = 38.62, longitude = 34.71, country = "Türkiye", admin1 = "Nevşehir"),
    City(id = 51, name = "Niğde", latitude = 37.97, longitude = 34.68, country = "Türkiye", admin1 = "Niğde"),
    City(id = 52, name = "Ordu", latitude = 40.98, longitude = 37.89, country = "Türkiye", admin1 = "Ordu"),
    City(id = 53, name = "Rize", latitude = 41.02, longitude = 40.52, country = "Türkiye", admin1 = "Rize"),
    City(id = 54, name = "Sakarya", latitude = 40.78, longitude = 30.4, country = "Türkiye", admin1 = "Sakarya"),
    City(id = 55, name = "Samsun", latitude = 41.28, longitude = 36.34, country = "Türkiye", admin1 = "Samsun"),
    City(id = 56, name = "Siirt", latitude = 37.93, longitude = 41.94, country = "Türkiye", admin1 = "Siirt"),
    City(id = 57, name = "Sinop", latitude = 42.03, longitude = 35.16, country = "Türkiye", admin1 = "Sinop"),
    City(id = 58, name = "Sivas", latitude = 39.75, longitude = 37.02, country = "Türkiye", admin1 = "Sivas"),
    City(id = 59, name = "Tekirdağ", latitude = 40.98, longitude = 27.51, country = "Türkiye", admin1 = "Tekirdağ"),
    City(id = 60, name = "Tokat", latitude = 40.31, longitude = 36.55, country = "Türkiye", admin1 = "Tokat"),
    City(id = 61, name = "Trabzon", latitude = 41.01, longitude = 39.73, country = "Türkiye", admin1 = "Trabzon"),
    City(id = 62, name = "Tunceli", latitude = 39.1, longitude = 39.54, country = "Türkiye", admin1 = "Tunceli"),
    City(id = 63, name = "Şanlıurfa", latitude = 37.17, longitude = 38.79, country = "Türkiye", admin1 = "Şanlıurfa"),
    City(id = 64, name = "Uşak", latitude = 38.67, longitude = 29.41, country = "Türkiye", admin1 = "Uşak"),
    City(id = 65, name = "Van", latitude = 38.49, longitude = 43.38, country = "Türkiye", admin1 = "Van"),
    City(id = 66, name = "Yozgat", latitude = 39.82, longitude = 34.8, country = "Türkiye", admin1 = "Yozgat"),
    City(id = 67, name = "Zonguldak", latitude = 41.45, longitude = 31.79, country = "Türkiye", admin1 = "Zonguldak"),
    City(id = 68, name = "Aksaray", latitude = 38.37, longitude = 34.03, country = "Türkiye", admin1 = "Aksaray"),
    City(id = 69, name = "Bayburt", latitude = 40.26, longitude = 40.22, country = "Türkiye", admin1 = "Bayburt"),
    City(id = 70, name = "Karaman", latitude = 37.18, longitude = 33.22, country = "Türkiye", admin1 = "Karaman"),
    City(id = 71, name = "Kırıkkale", latitude = 39.85, longitude = 33.51, country = "Türkiye", admin1 = "Kırıkkale"),
    City(id = 72, name = "Batman", latitude = 37.89, longitude = 41.13, country = "Türkiye", admin1 = "Batman"),
    City(id = 73, name = "Şırnak", latitude = 37.51, longitude = 42.45, country = "Türkiye", admin1 = "Şırnak"),
    City(id = 74, name = "Bartın", latitude = 41.64, longitude = 32.34, country = "Türkiye", admin1 = "Bartın"),
    City(id = 75, name = "Ardahan", latitude = 41.11, longitude = 42.7, country = "Türkiye", admin1 = "Ardahan"),
    City(id = 76, name = "Iğdır", latitude = 39.92, longitude = 44.05, country = "Türkiye", admin1 = "Iğdır"),
    City(id = 77, name = "Yalova", latitude = 40.66, longitude = 29.28, country = "Türkiye", admin1 = "Yalova"),
    City(id = 78, name = "Karabük", latitude = 41.2, longitude = 32.63, country = "Türkiye", admin1 = "Karabük"),
    City(id = 79, name = "Kilis", latitude = 36.72, longitude = 37.12, country = "Türkiye", admin1 = "Kilis"),
    City(id = 80, name = "Osmaniye", latitude = 37.07, longitude = 36.25, country = "Türkiye", admin1 = "Osmaniye"),
    City(id = 81, name = "Düzce", latitude = 40.84, longitude = 31.16, country = "Türkiye", admin1 = "Düzce")
)

// Arama kutusu boşken gösterilecek büyük şehirler (81 il listesinden seçiliyor)
val defaultCities = listOf(
    "İstanbul", "Ankara", "İzmir", "Bursa", "Antalya",
    "Adana", "Konya", "Gaziantep", "Şanlıurfa", "Kocaeli"
).mapNotNull { name -> turkishProvinces.find { it.name == name } }
