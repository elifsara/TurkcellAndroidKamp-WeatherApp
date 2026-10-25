package com.example.hava

import com.google.gson.annotations.SerializedName

// Detay ekranı için hava tahmini cevabı (anlık + saatlik + günlük)
data class ForecastResponse(
    val current: ForecastCurrent,
    val hourly: Hourly,
    val daily: Daily
)

data class ForecastCurrent(
    val time: String,                                                  // "2026-10-25T19:45"
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("apparent_temperature") val feelsLike: Double,    // hissedilen
    @SerializedName("relative_humidity_2m") val humidity: Int,        // nem %
    @SerializedName("wind_speed_10m") val windSpeed: Double,          // km/s
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("is_day") val isDay: Int
)

// Saatlik veriler: aynı sıradaki elemanlar aynı saate ait (time[5] ↔ temperature[5])
data class Hourly(
    val time: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("is_day") val isDay: List<Int>
)

// Günlük veriler: ilk eleman dün (past_days=1), ikincisi bugün, üçüncüsü yarın...
data class Daily(
    val time: List<String>,                                             // "2026-10-25"
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val maxTemperature: List<Double>,
    @SerializedName("temperature_2m_min") val minTemperature: List<Double>,
    @SerializedName("precipitation_probability_max") val rainChance: List<Int?>,
    val sunrise: List<String>,
    val sunset: List<String>
)
