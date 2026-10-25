package com.example.hava

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hava.ui.theme.HavaTheme

// Konum bulunmadan önce gösterilen kart: izin iste / aranıyor / izin yok / bulunamadı.
// Konum bulununca bunun yerine normal CityCard gösteriliyor (MainActivity'de).
@Composable
fun LocationPromptCard(
    state: LocationState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 110.dp)
                .background(Brush.horizontalGradient(listOf(Color(0xFF1E6FD9), Color(0xFF4FB3E8))))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state == LocationState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(36.dp))
            } else {
                Text("📍", style = MaterialTheme.typography.headlineLarge)
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = when (state) {
                        LocationState.Loading -> "Konumun bulunuyor…"
                        LocationState.Denied -> "Konum izni verilmedi"
                        LocationState.Unavailable -> "Konum bulunamadı"
                        else -> "Bulunduğun yerin havası"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = when (state) {
                        LocationState.Loading -> "Birkaç saniye sürebilir"
                        LocationState.Denied -> "İzni ayarlardan da açabilirsin"
                        LocationState.Unavailable -> "Konum servisinin açık olduğundan emin ol"
                        else -> "Konum izni verirsen en üstte gösteririz"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )

                if (state != LocationState.Loading) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        FilledTonalButton(
                            onClick = onRequestPermission,
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White)
                        ) {
                            Text(if (state == LocationState.NotAsked) "Konumumu kullan" else "Tekrar dene")
                        }
                        if (state == LocationState.Denied) {
                            TextButton(onClick = onOpenSettings) {
                                Text("Ayarlar", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun LocationPromptCardPreview() {
    HavaTheme {
        LocationPromptCard(state = LocationState.NotAsked, onRequestPermission = {}, onOpenSettings = {})
    }
}

@Preview
@Composable
fun LocationPromptCardDeniedPreview() {
    HavaTheme {
        LocationPromptCard(state = LocationState.Denied, onRequestPermission = {}, onOpenSettings = {})
    }
}
