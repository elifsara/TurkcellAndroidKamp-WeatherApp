package com.example.hava

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hava.ui.theme.HavaTheme


@Composable
fun CityCard(
    city: City,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick=onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),     //Kartların boyutunun sabit kalması için
        shape = RoundedCornerShape(24.dp)      //Yuvarlak köşeli
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Sol taraf: Şehir adı + Plaka
            Column(modifier = Modifier.weight(1f)) {
                // weight(1f) sağa kadar olan boş alanı kapla.
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Plaka: ${city.plate.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant   //Soluk renk
                )
            }
            // Sağ taraf Sıcaklık (Yer tutucu-şimdilik)
            Text(
                text = "--°",
                style = MaterialTheme.typography.displaySmall
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun CityCardPreview() {
    HavaTheme {
        CityCard(city = City("Ankara", 6))
    }
}