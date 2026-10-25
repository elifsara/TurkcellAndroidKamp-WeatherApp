package com.example.hava

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherCodeTest {

    @Test
    fun `sicaklik yuvarlanir, bilinmiyorsa cizgi gosterilir`() {
        assertEquals("21°", formatTemperature(20.6))
        assertEquals("-3°", formatTemperature(-3.2))
        assertEquals("--°", formatTemperature(null))
    }

    @Test
    fun `gece acik havada ay gosterilir`() {
        assertEquals("☀️", weatherEmoji(0, isDay = true))
        assertEquals("🌙", weatherEmoji(0, isDay = false))
        assertEquals("🌧️", weatherEmoji(63, isDay = false))   // yağmur gece de yağmur
    }

    @Test
    fun `hava kodu Turkce aciklamaya cevrilir`() {
        assertEquals("Açık", weatherDescription(0))
        assertEquals("Karlı", weatherDescription(73))
        assertEquals("Fırtına", weatherDescription(95))
    }
}
