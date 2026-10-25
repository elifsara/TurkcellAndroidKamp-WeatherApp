package com.example.hava

import kotlin.math.roundToInt

// 20.3 -> "20°", null -> "--°"
fun formatTemperature(temperature: Double?): String =
    if (temperature == null) "--°" else "${temperature.roundToInt()}°"

// WMO hava durumu kodları → emoji
fun weatherEmoji(code: Int?): String = when (code) {
    0 -> "☀️"
    1, 2 -> "🌤️"
    3 -> "☁️"
    45, 48 -> "🌫️"
    in 51..57 -> "🌦️"
    in 61..67, in 80..82 -> "🌧️"
    in 71..77, 85, 86 -> "❄️"
    in 95..99 -> "⛈️"
    else -> ""
}

// WMO hava durumu kodları → Türkçe açıklama
fun weatherDescription(code: Int?): String = when (code) {
    0 -> "Açık"
    1, 2 -> "Parçalı bulutlu"
    3 -> "Kapalı"
    45, 48 -> "Sisli"
    in 51..57 -> "Çisenti"
    in 61..67 -> "Yağmurlu"
    in 71..77 -> "Karlı"
    in 80..82 -> "Sağanak"
    85, 86 -> "Kar sağanağı"
    in 95..99 -> "Fırtına"
    else -> ""
}
