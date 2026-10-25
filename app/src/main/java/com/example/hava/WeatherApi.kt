package com.example.hava

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    // Tam adres: https://api.open-meteo.com/v1/forecast?latitude=..&longitude=..&current=temperature_2m,weather_code
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,weather_code"
    ): WeatherResponse
}
