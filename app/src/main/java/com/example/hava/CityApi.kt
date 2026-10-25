package com.example.hava

import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit'e hangi adrese, hangi parametrelerle istek atılacağını anlatan "menü"
interface CityApi {

    // Tam adres: https://geocoding-api.open-meteo.com/v1/search?name==...&count=20&language=tr
    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int = 20,
        @Query("language") language: String = "tr"
    ): CitySearchResponse
}