package com.example.hava
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// Uygulama boyunca tek bir Retrofit nesnesi kullanalım (object = tek kopya)
object ApiClient {

    private const val BASE_URL = "https://geocoding-api.open-meteo.com/"   // sonundaki / zorunlu!

    val cityApi: CityApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())   // JSON → Kotlin nesnesi
        .build()
        .create(CityApi::class.java)
}