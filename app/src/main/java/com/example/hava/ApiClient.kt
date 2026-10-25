package com.example.hava
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// Uygulama boyunca tek bir Retrofit nesnesi kullanalım (object = tek kopya)
object ApiClient {

    private const val BASE_URL = "https://geocoding-api.open-meteo.com/"   // sonundaki / zorunlu!

    // Giden isteği ve gelen JSON'u Logcat'e yazar (Logcat'te "tag:okhttp" ile filtrele)
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY   // adres + başlıklar + JSON gövdesi
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val cityApi: CityApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())   // JSON → Kotlin nesnesi
        .build()
        .create(CityApi::class.java)
}
