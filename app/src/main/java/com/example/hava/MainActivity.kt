package com.example.hava

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hava.ui.theme.HavaTheme

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
fun CityScreen(
    modifier: Modifier = Modifier,
    viewModel: CityViewModel = viewModel()     // ViewModel'i Compose'dan al
) {
    // Aramadan sonra klavyeyi kapatmak için
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        // Üst kısım: arama kutusu + Ara düğmesi yan yana
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = viewModel.query,    //kutuda görünen metin
                onValueChange = { viewModel.onQueryChange(it) },   //kullanıcı yazdıkça state'i güncelle.
                label = { Text("Şehir ara") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),   // Enter tuşu 🔍 olur
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.search()
                    keyboardController?.hide()
                }),
                trailingIcon = {
                    if (viewModel.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Text("✕")
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                viewModel.search()
                keyboardController?.hide()
            }) {
                Text("Ara")
            }
        }

        // Alt kısım: duruma göre farklı ekran
        when (val state = viewModel.uiState) {
            CityUiState.Loading -> LoadingView()
            CityUiState.Empty -> MessageView(
                if (viewModel.query.trim().length < 3) "Aramak için en az 3 harf yaz"
                else "Şehir bulunamadı"
            )
            is CityUiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.search() }
            )
            is CityUiState.Success -> {
                Text(
                    text = "${state.cities.size} şehir",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, top = 8.dp)
                )
                CityList(
                    cities = state.cities,
                    selectedCityId = viewModel.selectedCityId,
                    onCityClick = { viewModel.onCityClick(it) }
                )
            }
        }
    }
}


@Composable
fun CityList(
    cities: List<City>,
    selectedCityId: Long?,
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
                isSelected = city.id == selectedCityId,
                onClick ={ onCityClick(city)}
            )
        }
    }
}
