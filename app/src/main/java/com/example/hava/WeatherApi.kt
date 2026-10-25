package com.example.hava

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    // Tam adres: https://api.open-meteo.com/v1/forecast?latitude=..&longitude=..&current=temperature_2m,weather_code,is_day
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,weather_code,is_day"
    ): WeatherResponse

    // Detay ekranı: anlık + 24 saatlik + dün/bugün/yarın ve sonraki günler
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code,is_day",
        @Query("hourly") hourly: String = "temperature_2m,weather_code,is_day",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,sunrise,sunset",
        @Query("timezone") timezone: String = "auto",   // saatler şehrin yerel saatiyle gelsin
        @Query("past_days") pastDays: Int = 1,          // dünü de getir
        @Query("forecast_days") forecastDays: Int = 7
    ): ForecastResponse
}
