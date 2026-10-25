package com.example.hava

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hava.ui.theme.HavaTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.material3.IconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HavaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CityScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CityScreen(modifier: Modifier = Modifier) {
    // Kutuya yazılan metin. Başta boş.
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCityName by rememberSaveable { mutableStateOf<String?>(null) }
    // Yazılan metni içeren şehilrler
    val filteredCities = cities.filter { it.name.contains(query, ignoreCase = true) }
    Column(modifier = modifier) {
        OutlinedTextField(
            value = query,    //kutuda görünen metin
            onValueChange = { query = it },   //kullanıcı yazdıkça state'i güncelle.
            label = { Text("Şehir ara") },
            singleLine = true,
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Text("✕")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp)
        )
        Text(
            text = "${filteredCities.size} şehir",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 20.dp, top = 8.dp)
        )

        if (filteredCities.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Şehir bulunamadı",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            CityList(
                cities = filteredCities,
                selectedCityName = selectedCityName,
                onCityClick = { city ->
                        selectedCityName = city.name
                        Log.d("HavaApp", "Tıklandı: ${city.name}")
                }
            )
        }
    }
}


@Composable
fun CityList(
    cities: List<City>,
    selectedCityName: String?,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cities) { city ->
            CityCard(
                city = city,
                isSelected = city.name == selectedCityName,
                onClick ={ onCityClick(city)}
            )
        }
    }
}