package com.example.hava

import android.os.Bundle
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hava.ui.theme.HavaTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HavaTheme {
                // Liste aşağı kaydırılınca büyük başlık küçülsün
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.app_name),   // "Hava Durumu"
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            scrollBehavior = scrollBehavior
                        )
                    }
                ) { innerPadding ->
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
        // Üst kısım: yuvarlak arama çubuğu (yazdıkça aradığı için ayrı bir Ara düğmesine gerek yok)
        OutlinedTextField(
            value = viewModel.query,    //kutuda görünen metin
            onValueChange = { viewModel.onQueryChange(it) },   //kullanıcı yazdıkça state'i güncelle.
            placeholder = { Text("Şehir ara") },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (viewModel.query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Temizle")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),   // Enter tuşu 🔍 olur
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.search()
                keyboardController?.hide()
            }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        // Alt kısım: duruma göre farklı ekran
        when (val state = viewModel.uiState) {
            CityUiState.Loading -> LoadingView()
            CityUiState.Empty -> MessageView("Şehir bulunamadı")
            is CityUiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.search() }
            )
            is CityUiState.Success -> {
                Text(
                    text = if (viewModel.query.isBlank()) "Popüler şehirler" else "${state.cities.size} şehir",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp)
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
