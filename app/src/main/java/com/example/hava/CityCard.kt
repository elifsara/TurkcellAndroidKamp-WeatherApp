package com.example.hava

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hava.ui.theme.HavaTheme


@Composable
fun CityCard(
    city: City,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp),     //Kartların boyutunun sabit kalması için
        shape = RoundedCornerShape(24.dp),      //Yuvarlak köşeli
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),   // rengi arkadaki gradient veriyor
        border = if (isSelected) BorderStroke(3.dp, Color.White) else null       // seçili kartta beyaz çerçeve
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                // Havaya göre soldan sağa renk geçişi (WeatherCode.kt → weatherGradient)
                .background(Brush.horizontalGradient(weatherGradient(city.weatherCode, city.isDay)))
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Sol taraf: Şehir adı, bölge, hava durumu
            Column(modifier = Modifier.weight(1f)) {
                // weight(1f) sağa kadar olan boş alanı kapla.
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis     // sığmazsa "..." ile kes
                )
                Text(
                    text = listOfNotNull(city.admin1, city.country).joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),   //Soluk beyaz
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${weatherEmoji(city.weatherCode, city.isDay)} ${weatherDescription(city.weatherCode)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Sağ taraf: büyük sıcaklık
            Text(
                text = formatTemperature(city.temperature),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Light,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CityCardPreview() {
    HavaTheme {
        CityCard(city = City(
            id = 1,
            name = "Ankara",
            latitude = 39.92,
            longitude = 32.85,
            country = "Türkiye",
            admin1 = "Ankara",
            temperature = 20.3,
            weatherCode = 0,
            isDay = true
        ))
    }
}

@Preview(showBackground = true)
@Composable
fun CityCardSelectedPreview() {
    HavaTheme {
        CityCard(city = City(
            id = 1,
            name = "Ankara",
            latitude = 39.92,
            longitude = 32.85,
            country = "Türkiye",
            admin1 = "Ankara",
            temperature = 20.3,
            weatherCode = 0,
            isDay = true
        ), isSelected = true)
    }
}

@Preview(showBackground = true)
@Composable
fun CityCardRainPreview() {
    HavaTheme {
        CityCard(city = City(
            id = 2,
            name = "Trabzon",
            latitude = 41.0,
            longitude = 39.73,
            country = "Türkiye",
            admin1 = "Trabzon",
            temperature = 11.6,
            weatherCode = 61,
            isDay = true
        ))
    }
}
