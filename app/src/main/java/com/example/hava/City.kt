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
