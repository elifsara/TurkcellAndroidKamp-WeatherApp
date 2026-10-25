package com.example.hava

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hava.ui.theme.HavaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LifecycleLog.add("MainActivity onCreate")
        // Üstteki saat/pil simgeleri mavi başlık üzerinde okunsun diye beyaz olsun
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        setContent {
            HavaTheme {
                // Scaffold + TopAppBar yerine kendi başlık alanımızı (SearchHeader) kullanıyoruz
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CityScreen()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LifecycleLog.add("MainActivity onStart")
    }

    // Detay ekranı açılınca MainActivity ölmez, sadece arka plana geçer (onStop)
    override fun onStop() {
        super.onStop()
        LifecycleLog.add("MainActivity onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        val reason = if (isChangingConfigurations) "döndürme, yeniden doğacak" else "kapandı"
        LifecycleLog.add("MainActivity onDestroy ($reason)")
    }
}

@OptIn(ExperimentalMaterial3Api::class)   // PullToRefreshBox henüz "deneysel" işaretli
@Composable
fun CityScreen(
    modifier: Modifier = Modifier,
    viewModel: CityViewModel = viewModel()     // ViewModel'i Compose'dan al
) {
    // Aramadan sonra klavyeyi kapatmak için
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        // Üst kısım: renkli başlık alanı + arama kutusu (SearchHeader.kt)
        SearchHeader(
            query = viewModel.query,
            onQueryChange = { viewModel.onQueryChange(it) },
            onSearch = {
                viewModel.search()
                keyboardController?.hide()
            }
        )

        // Alt kısım: duruma göre farklı ekran. Durum değişince eski ekran yavaşça kaybolur, yenisi belirir.
        AnimatedContent(
            targetState = viewModel.uiState,
            contentKey = { it::class },   // liste → yeni liste geçişinde tüm ekranı değil, sadece kartları anime et
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
            label = "durum",
            modifier = Modifier.navigationBarsPadding()   // alttaki gezinme çubuğunun altında kalmasın
        ) { state ->
            when (state) {
                CityUiState.Loading -> LoadingView()
                CityUiState.Empty -> MessageView("Şehir bulunamadı")
                is CityUiState.Error -> ErrorView(
                    message = state.message,
                    onRetry = { viewModel.search() }
                )
                is CityUiState.Success -> Column {
                    Text(
                        text = if (viewModel.query.isBlank()) "Popüler şehirler" else "${state.cities.size} şehir",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 20.dp, top = 16.dp)
                    )
                    // Listeyi aşağı çekince sıcaklıkları yenile
                    PullToRefreshBox(
                        isRefreshing = viewModel.isRefreshing,
                        onRefresh = { viewModel.refresh() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CityList(
                            cities = state.cities.map { viewModel.withWeather(it) },   // yüklenen sıcaklıkları ekle
                            selectedCityId = viewModel.selectedCityId,
                            onCityClick = {
                                viewModel.onCityClick(it)
                                context.startActivity(DetailActivity.newIntent(context, it))   // detay ekranını aç
                            },
                            onCityVisible = { viewModel.loadWeather(it) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CityList(
    cities: List<City>,
    selectedCityId: Long?,
    onCityClick: (City) -> Unit,
    onCityVisible: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cities, key = { it.id }) { city ->   // key: hangi kartın hangi şehir olduğunu takip et
            // LazyColumn bu kartı ancak ekrana gelirken çizer → o an sıcaklığını iste.
            // Ekrana hiç gelmeyen kartların hava durumu hiç istenmez.
            LaunchedEffect(city.id) {
                onCityVisible(city)
            }
            CityCard(
                city = city,
                isSelected = city.id == selectedCityId,
                onClick ={ onCityClick(city)},
                modifier = Modifier.animateItem()     // liste değişince kartlar kayarak yer değiştirsin
            )
        }
    }
}
