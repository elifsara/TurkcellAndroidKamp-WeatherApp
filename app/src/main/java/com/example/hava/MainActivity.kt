package com.example.hava

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.core.content.ContextCompat
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

    // Konum izni penceresi. Sonuç gelince: izin verildiyse konumu bul, verilmediyse kartta söyle.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) viewModel.loadMyLocation(force = true) else viewModel.onLocationDenied()
    }
    val requestLocation = {
        if (hasLocationPermission(context)) {
            viewModel.loadMyLocation(force = true)      // izin zaten var (ör. ayarlardan açıldı)
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            )
        }
    }

    // Daha önce izin verildiyse uygulama açılır açılmaz konumu bul (tekrar sormadan)
    LaunchedEffect(Unit) {
        if (hasLocationPermission(context)) viewModel.loadMyLocation()
    }

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
                is CityUiState.Success -> {
                    // Kutu boşsa: önce favoriler, sonra popüler şehirler. Aramada: sadece sonuçlar.
                    val sections = if (viewModel.query.isBlank()) {
                        val favoriteIds = viewModel.favorites.map { it.id }.toSet()
                        listOf(
                            CitySection("favoriler", "❤️ Favorilerim", viewModel.favorites),
                            CitySection("populer", "Popüler şehirler", state.cities.filterNot { it.id in favoriteIds })
                        )
                    } else {
                        listOf(CitySection("sonuclar", "${state.cities.size} şehir", state.cities))
                    }.filter { it.cities.isNotEmpty() }   // boş bölümün başlığını gösterme

                    // Listeyi aşağı çekince sıcaklıkları yenile
                    PullToRefreshBox(
                        isRefreshing = viewModel.isRefreshing,
                        onRefresh = { viewModel.refresh(sections.flatMap { it.cities }) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CityList(
                            sections = sections.map { section ->
                                section.copy(cities = section.cities.map { viewModel.withWeather(it) })   // yüklenen sıcaklıkları ekle
                            },
                            selectedCityId = viewModel.selectedCityId,
                            isFavorite = { viewModel.isFavorite(it) },
                            onFavoriteClick = { viewModel.toggleFavorite(it) },
                            onCityClick = {
                                viewModel.onCityClick(it)
                                context.startActivity(DetailActivity.newIntent(context, it))   // detay ekranını aç
                            },
                            onCityVisible = { viewModel.loadWeather(it) },
                            // Kutu boşken en üstte "Konumum" kartı
                            locationState = if (viewModel.query.isBlank()) viewModel.locationState else null,
                            onRequestLocation = requestLocation,
                            onOpenSettings = { context.startActivity(appSettingsIntent(context)) },
                            withWeather = { viewModel.withWeather(it) }
                        )
                    }
                }
            }
        }
    }
}

// Listede başlığıyla birlikte bir grup şehir (ör. "❤️ Favorilerim")
data class CitySection(
    val key: String,          // LazyColumn için değişmeyen kimlik
    val title: String,
    val cities: List<City>
)


@Composable
fun CityList(
    sections: List<CitySection>,
    selectedCityId: Long?,
    isFavorite: (City) -> Boolean,
    onFavoriteClick: (City) -> Unit,
    onCityClick: (City) -> Unit,
    onCityVisible: (City) -> Unit,
    locationState: LocationState?,          // null → konum kartı gösterilmez (arama yaparken)
    onRequestLocation: () -> Unit,
    onOpenSettings: () -> Unit,
    withWeather: (City) -> City,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Favori eklenip çıkarılınca en üste kaydır. Yoksa LazyColumn bulunduğu kartta kalır ve
    // üste eklenen "Favorilerim" bölümü ekranın dışında kalır.
    val favoriteCount = sections.find { it.key == "favoriler" }?.cities?.size ?: 0
    LaunchedEffect(favoriteCount) {
        listState.animateScrollToItem(0)
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (locationState != null) {
            item(key = "konum") {
                if (locationState is LocationState.Found) {
                    val city = locationState.city
                    LaunchedEffect(city.latitude, city.longitude) { onCityVisible(city) }
                    CityCard(
                        city = withWeather(city),
                        isSelected = city.id == selectedCityId,
                        onClick = { onCityClick(city) },   // kalp yok: konum favorilere eklenmez
                        modifier = Modifier.animateItem()
                    )
                } else {
                    LocationPromptCard(
                        state = locationState,
                        onRequestPermission = onRequestLocation,
                        onOpenSettings = onOpenSettings,
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }

        sections.forEach { section ->
            // Bölüm başlığı
            item(key = section.key) {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .animateItem()
                        .padding(start = 4.dp, top = 8.dp)
                )
            }
            items(section.cities, key = { it.id }) { city ->   // key: hangi kartın hangi şehir olduğunu takip et
                // LazyColumn bu kartı ancak ekrana gelirken çizer → o an sıcaklığını iste.
                // Ekrana hiç gelmeyen kartların hava durumu hiç istenmez.
                LaunchedEffect(city.id) {
                    onCityVisible(city)
                }
                CityCard(
                    city = city,
                    isSelected = city.id == selectedCityId,
                    isFavorite = isFavorite(city),
                    onFavoriteClick = { onFavoriteClick(city) },
                    onClick = { onCityClick(city) },
                    modifier = Modifier.animateItem()     // liste değişince kartlar kayarak yer değiştirsin
                )
            }
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean =
    listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).any {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

// Uygulamanın Android ayarlarındaki sayfası (izinler buradan açılabiliyor)
private fun appSettingsIntent(context: Context) =
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
