package com.example.hava.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue80,
    primaryContainer = SkyBlueContainer80,
    secondary = Slate80,
    tertiary = Sun80,
    background = Background80,
    surface = Background80
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue40,
    primaryContainer = SkyBlueContainer40,
    secondary = Slate40,
    tertiary = Sun40,
    background = Background40,
    surface = Background40
)

@Composable
fun HavaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color (Android 12+) telefonun duvar kağıdından renk alır.
    // Kendi mavi tonlarımız her telefonda aynı görünsün diye kapalı.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
