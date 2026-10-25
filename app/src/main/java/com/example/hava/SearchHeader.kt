package com.example.hava

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hava.ui.theme.HavaTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Ekranın üstü: gökyüzü renginde arka plan + başlık + bugünün tarihi + arama kutusu
@Composable
fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Açık temada gök mavisi (uygulama ikonuyla aynı), koyu temada gece laciverti
    val colors = if (isSystemInDarkTheme()) {
        listOf(Color(0xFF0F2027), Color(0xFF1B3A5C))
    } else {
        listOf(Color(0xFF1E6FD9), Color(0xFF4FB3E8))
    }

    // Örn. "25 Ekim Cumartesi"
    val today = remember {
        SimpleDateFormat("d MMMM EEEE", Locale.forLanguageTag("tr")).format(Date())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))   // sadece alt köşeler yuvarlak
            .background(Brush.verticalGradient(colors))
            .statusBarsPadding()            // saat/pil çubuğunun altından başla (arka plan onun arkasına da uzanır)
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.app_name),   // "Hava Durumu"
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = today,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.85f)
        )

        // Beyaz, yuvarlak arama kutusu (yazdıkça aradığı için ayrı bir Ara düğmesine gerek yok)
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Şehir ara") },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Temizle")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),   // Enter tuşu 🔍 olur
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = Color.Transparent,     // çerçeve yok, sadece dolu kutu
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }
}

@Preview
@Composable
fun SearchHeaderPreview() {
    HavaTheme {
        SearchHeader(query = "", onQueryChange = {}, onSearch = {})
    }
}
