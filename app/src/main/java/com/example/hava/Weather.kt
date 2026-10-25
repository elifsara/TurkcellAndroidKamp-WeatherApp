package com.example.hava

import com.google.gson.annotations.SerializedName

// Hava durumu API'sinin cevabı (biz sadece "current" kısmını kullanıyoruz)
data class WeatherResponse(
    val current: CurrentWeather?
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double,   // JSON'daki ad farklı → eşleştiriyoruz
    @SerializedName("weather_code") val weatherCode: Int
)
