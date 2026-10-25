package com.example.hava

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

// 20.3 -> "20°", null -> "--°"
fun formatTemperature(temperature: Double?): String =
    if (temperature == null) "--°" else "${temperature.roundToInt()}°"

// WMO hava durumu kodları → emoji (gece açık/az bulutluysa ay)
fun weatherEmoji(code: Int?, isDay: Boolean? = true): String = when (code) {
    0 -> if (isDay == false) "🌙" else "☀️"
    1, 2 -> if (isDay == false) "🌙" else "🌤️"
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

// Hava durumuna göre kartın arka plan renk geçişi (soldan sağa iki renk).
// Hepsi beyaz yazının okunacağı kadar koyu seçildi.
fun weatherGradient(code: Int?, isDay: Boolean?): List<Color> = when {
    code == null -> listOf(Color(0xFF5C6B7A), Color(0xFF8A9BA8))                      // bilinmiyor: gri-mavi
    isDay == false -> listOf(Color(0xFF0F2027), Color(0xFF2C5364))                    // gece: lacivert
    code == 0 -> listOf(Color(0xFFE65C00), Color(0xFFF5A623))                         // açık: turuncu
    code in 1..2 -> listOf(Color(0xFF1E6FD9), Color(0xFF4FB3E8))                      // parçalı bulutlu: gök mavisi
    code == 3 || code in 45..48 -> listOf(Color(0xFF4B5A73), Color(0xFF7D8BA1))       // kapalı / sis: gri
    code in 71..77 || code in 85..86 -> listOf(Color(0xFF5A76A8), Color(0xFF8AA6D1))  // kar: buz mavisi
    code in 95..99 -> listOf(Color(0xFF232526), Color(0xFF414345))                    // fırtına: koyu gri
    else -> listOf(Color(0xFF2B4170), Color(0xFF4A6CA8))                              // yağmur / çisenti: koyu mavi
}
