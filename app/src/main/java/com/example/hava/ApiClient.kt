package com.example.hava
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// Uygulama boyunca tek bir Retrofit nesnesi kullanalım (object = tek kopya)
object ApiClient {

    private const val GEOCODING_URL = "https://geocoding-api.open-meteo.com/"   // şehir arama (sonundaki / zorunlu!)
    private const val WEATHER_URL = "https://api.open-meteo.com/"               // hava durumu

    // Giden isteği ve gelen JSON'u Logcat'e yazar (Logcat'te "tag:okhttp" ile filtrele)
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY   // adres + başlıklar + JSON gövdesi
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Aynı ayarlarla farklı adreslere Retrofit kurmak için yardımcı fonksiyon
    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())   // JSON → Kotlin nesnesi
        .build()

    val cityApi: CityApi = retrofit(GEOCODING_URL).create(CityApi::class.java)
    val weatherApi: WeatherApi = retrofit(WEATHER_URL).create(WeatherApi::class.java)
}
