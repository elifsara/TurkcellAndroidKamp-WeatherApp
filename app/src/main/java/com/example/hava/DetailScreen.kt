package com.example.hava

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

private val turkish = Locale.forLanguageTag("tr")

@Composable
fun DetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val state = viewModel.uiState
    val forecast = (state as? DetailUiState.Success)?.forecast

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        DetailHeader(
            city = viewModel.city,
            forecast = forecast,
            isFavorite = viewModel.isFavorite,
            onFavoriteClick = { viewModel.toggleFavorite() },
            onBack = onBack
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when (state) {
                DetailUiState.Loading -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                is DetailUiState.Error -> Box(modifier = Modifier.height(300.dp)) {
                    ErrorView(message = state.message, onRetry = { viewModel.load() })
                }
                is DetailUiState.Success -> {
                    HourlySection(state.forecast)
                    YesterdayTodayTomorrow(state.forecast.daily)
                    NextDaysSection(state.forecast.daily)
                    DetailsGrid(state.forecast)
                }
            }
        }
    }
}

// Üst kısım: havaya göre renkli arka plan, şehir adı ve büyük sıcaklık
@Composable
private fun DetailHeader(
    city: City,
    forecast: ForecastResponse?,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onBack: () -> Unit
) {
    val current = forecast?.current
    val isDay = current?.let { it.isDay == 1 }
    val today = 1   // daily[0] = dün, daily[1] = bugün

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.verticalGradient(weatherGradient(current?.weatherCode, isDay)))
            .statusBarsPadding()
            .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
            }
            Spacer(modifier = Modifier.weight(1f))
            if (city.id != MY_LOCATION_ID) {   // "Konumum" favorilere eklenmez
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Favorilerden çıkar" else "Favorilere ekle",
                        tint = Color.White
                    )
                }
            }
        }
        Text(
            text = city.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Text(
            text = listOfNotNull(city.admin1, city.country).distinct().joinToString(", "),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = "${weatherEmoji(current?.weatherCode, isDay)} ${formatTemperature(current?.temperature)}",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Light,
            color = Color.White,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            text = weatherDescription(current?.weatherCode),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        if (forecast != null && forecast.daily.time.size > today) {
            Text(
                text = "En yüksek ${formatTemperature(forecast.daily.maxTemperature[today])}  ·  " +
                        "En düşük ${formatTemperature(forecast.daily.minTemperature[today])}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

// Önümüzdeki 24 saat, yana kaydırılan liste
@Composable
private fun HourlySection(forecast: ForecastResponse) {
    val hourly = forecast.hourly
    // Şu anki saatin listedeki yerini bul ("2026-10-25T19:45" → "2026-10-25T19:00")
    val start = hourly.time.indexOf(forecast.current.time.take(13) + ":00").coerceAtLeast(0)
    val hours = (start until minOf(start + 24, hourly.time.size)).toList()

    Column {
        SectionTitle("Saatlik tahmin")
        Card(shape = RoundedCornerShape(24.dp)) {
            LazyRow(
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(hours) { i ->
                    Column(
                        modifier = Modifier.width(56.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (i == start) "Şimdi" else hourly.time[i].takeLast(5),   // "20:00"
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (i == start) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = weatherEmoji(hourly.weatherCode[i], hourly.isDay[i] == 1),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = formatTemperature(hourly.temperature[i]),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// Yan yana üç kart: dün, bugün (vurgulu), yarın
@Composable
private fun YesterdayTodayTomorrow(daily: Daily) {
    Column {
        SectionTitle("Dün · Bugün · Yarın")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("Dün", "Bugün", "Yarın").forEachIndexed { i, label ->
                if (i < daily.time.size) {
                    DayCard(
                        label = label,
                        date = formatDate(daily.time[i], "d MMM"),
                        weatherCode = daily.weatherCode[i],
                        max = daily.maxTemperature[i],
                        min = daily.minTemperature[i],
                        highlighted = i == 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCard(
    label: String,
    date: String,
    weatherCode: Int,
    max: Double,
    min: Double,
    highlighted: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = if (highlighted) {
            CardDefaults.cardColors(containerColor = Color.Transparent)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        val textColor = if (highlighted) Color.White else MaterialTheme.colorScheme.onSurface
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (highlighted) Modifier.background(Brush.verticalGradient(weatherGradient(weatherCode, true)))
                    else Modifier
                )
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = textColor)
            Text(date, style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.75f))
            Text(weatherEmoji(weatherCode), style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "${formatTemperature(max)} / ${formatTemperature(min)}",
                style = MaterialTheme.typography.titleSmall,
                color = textColor
            )
        }
    }
}

// Yarından sonraki günler (alt alta satırlar)
@Composable
private fun NextDaysSection(daily: Daily) {
    if (daily.time.size <= 3) return
    Column {
        SectionTitle("Önümüzdeki günler")
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                for (i in 3 until daily.time.size) {
                    if (i > 3) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDate(daily.time[i], "EEEE"),   // "Pazartesi"
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        val rain = daily.rainChance.getOrNull(i) ?: 0
                        Text(
                            text = if (rain >= 10) "💧%$rain" else "",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(56.dp)
                        )
                        Text(weatherEmoji(daily.weatherCode[i]), style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "${formatTemperature(daily.minTemperature[i])}  ${formatTemperature(daily.maxTemperature[i])}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.width(80.dp)
                        )
                    }
                }
            }
        }
    }
}

// 2x2 küçük bilgi kutuları
@Composable
private fun DetailsGrid(forecast: ForecastResponse) {
    val current = forecast.current
    val daily = forecast.daily
    Column {
        SectionTitle("Ayrıntılar")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoTile("🌡️", "Hissedilen", formatTemperature(current.feelsLike), Modifier.weight(1f))
                InfoTile("💧", "Nem", "%${current.humidity}", Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoTile("💨", "Rüzgâr", "${current.windSpeed.toInt()} km/s", Modifier.weight(1f))
                InfoTile(
                    "🌅", "Gün doğumu / batımı",
                    "${daily.sunrise[1].takeLast(5)} · ${daily.sunset[1].takeLast(5)}",
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun InfoTile(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$emoji  $label",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// "2026-10-25" → "25 Eki" veya "Pazar"
private fun formatDate(date: String, pattern: String): String = try {
    val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)
    if (parsed == null) date else SimpleDateFormat(pattern, turkish).format(parsed)
} catch (e: Exception) {
    date
}
