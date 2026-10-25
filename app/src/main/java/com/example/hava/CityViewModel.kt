package com.example.hava

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

// repository dışarıdan verilebiliyor → testlerde sahte (fake) bir Repository verebiliyoruz
class CityViewModel(
    private val repository: CityRepository = CityRepository()   // Ekran → ViewModel → Repository → API / veritabanı
) : ViewModel() {

    // Arama kutusundaki metin. Boşken popüler şehirler gösterilir.
    var query by mutableStateOf("")
        private set                       // dışarıdan sadece okunabilir, değiştirmek için fonksiyon

    // Ekranın şu anki durumu
    var uiState by mutableStateOf<CityUiState>(CityUiState.Loading)
        private set

    // Seçili şehrin id'si (artık ad yerine benzersiz id kullanıyoruz)
    var selectedCityId by mutableStateOf<Long?>(null)
        private set

    // Devam eden arama işi (yenisi başlayınca eskisini iptal edebilmek için)
    private var searchJob: Job? = null

    // Şehir id'si → hava durumu. Bir kere gelen tekrar istenmez (aramalar arasında da saklanır).
    private val weatherById = mutableStateMapOf<Long, CurrentWeather>()

    // Hava durumu şu an istenmekte olan şehirler (aynı şehir için iki istek gitmesin)
    private val loadingWeather = mutableSetOf<Long>()

    // Ekrana en az bir kez gelmiş şehirler (yenilerken sadece bunları tekrar isteriz)
    private val shownCityIds = mutableSetOf<Long>()

    // Aşağı çekip yenileme sürüyor mu? (true iken listenin üstünde dönen çember görünür)
    var isRefreshing by mutableStateOf(false)
        private set

    // Favori şehirler (veritabanından). Kalp simgesine basınca bu liste kendiliğinden güncellenir.
    var favorites by mutableStateOf<List<City>>(emptyList())
        private set

    // "Konumum" kartının durumu
    var locationState by mutableStateOf<LocationState>(LocationState.NotAsked)
        private set

    init {
        LifecycleLog.add("CityViewModel oluşturuldu")
        search()   // ViewModel ilk oluşturulduğunda bir kere ara

        // Veritabanını dinle: favori eklenince/silinince yeni liste buraya gelir
        viewModelScope.launch {
            repository.favorites.collect { favorites = it }
        }
    }

    // Sadece MainActivity gerçekten kapanınca çağrılır (döndürmede çağrılmaz)
    override fun onCleared() {
        LifecycleLog.add("CityViewModel silindi (onCleared)")
    }

    fun onQueryChange(newQuery: String){
        query = newQuery
        search(debounceMillis = 400)   // yazmayı bırakınca 400 ms sonra ara
    }

    fun onCityClick(city: City) {
        selectedCityId = city.id
        Log.d("HavaApp", "Tıklandı: ${city.name}")
    }

    fun search(debounceMillis: Long = 0) {
        searchJob?.cancel()                          // önceki aramayı iptal et
        searchJob = viewModelScope.launch {     // arka planda bir iş başlat
            delay(debounceMillis)                    // kullanıcı yazmaya devam ederse burada iptal olur

            val text = query.trim()
            if (text.isEmpty()) {                    // kutu boş → popüler şehirler (telefonda hazır, beklemeye gerek yok)
                uiState = CityUiState.Success(repository.getDefaultCities())
                return@launch
            }

            uiState = CityUiState.Loading
            uiState = try {
                val cities = repository.searchCities(text)
                if (cities.isEmpty()){
                    CityUiState.Empty
                } else {
                    CityUiState.Success(cities)
                }
            } catch (e: CancellationException) {
                throw e                              // iptal bir hata değil, aynen geri fırlat
            } catch (e: IOException) {                   // internet yok, zaman aşımı..
                CityUiState.Error("İnternet bağlantını kontrol et.")
            } catch (e: Exception) {                  // diğer her şey
                CityUiState.Error("Bir hata oluştu: ${e.message}")
            }
        }
    }

    // Kart ekrana gelince çağrılır: o şehrin hava durumunu (daha önce gelmediyse) iste
    fun loadWeather(city: City) {
        shownCityIds += city.id
        if (city.id in weatherById || city.id in loadingWeather) return
        loadingWeather += city.id
        Log.d("HavaApp", "Hava durumu isteniyor: ${city.name}")
        viewModelScope.launch {
            try {
                fetchWeather(city)
            } finally {
                loadingWeather -= city.id
            }
        }
    }

    // İzin verildikten sonra çağrılır. Konum zaten bulunduysa (ör. ekran döndü) tekrar aramaz.
    fun loadMyLocation(force: Boolean = false) {
        if (!force && (locationState is LocationState.Found || locationState == LocationState.Loading)) return
        locationState = LocationState.Loading
        viewModelScope.launch {
            locationState = try {
                repository.findMyCity()?.let { city ->
                    weatherById.remove(MY_LOCATION_ID)   // yer değişmiş olabilir → eski sıcaklığı unut
                    LocationState.Found(city)
                } ?: LocationState.Unavailable
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                LocationState.Unavailable
            }
        }
    }

    fun onLocationDenied() {
        locationState = LocationState.Denied
    }

    fun isFavorite(city: City): Boolean = favorites.any { it.id == city.id }

    // Kalbe basınca: favoriyse sil, değilse ekle
    fun toggleFavorite(city: City) {
        viewModelScope.launch {
            if (isFavorite(city)) repository.removeFavorite(city.id) else repository.addFavorite(city)
        }
    }

    // Liste aşağı çekilince: listedeki, ekrana gelmiş şehirlerin sıcaklıklarını yeniden iste
    // ("--°" kalanlar dahil; eski sıcaklıklar yenisi gelene kadar görünmeye devam eder)
    fun refresh(listedCities: List<City>) {
        val cities = listedCities.filter { it.id in shownCityIds }
        if (isRefreshing) return

        isRefreshing = true
        if (locationState is LocationState.Found) loadMyLocation(force = true)   // yer değişmiş olabilir
        Log.d("HavaApp", "Yenileniyor: ${cities.size} şehir")
        viewModelScope.launch {
            cities.map { city -> async { fetchWeather(city) } }.awaitAll()   // hepsi aynı anda
            isRefreshing = false
        }
    }

    // Tek bir şehrin hava durumunu iste ve sakla
    private suspend fun fetchWeather(city: City) {
        try {
            repository.getWeather(city)?.let { weatherById[city.id] = it }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Gelmezse kart "--°" kalır (ya da eski sıcaklığı gösterir); aşağı çekip yenileyince tekrar denenir
        }
    }

    // Şehre, yüklendiyse hava durumunu ekle (yüklenmediyse şehir olduğu gibi döner → kart "--°" gösterir)
    fun withWeather(city: City): City {
        val weather = weatherById[city.id] ?: return city
        return city.copy(
            temperature = weather.temperature,
            weatherCode = weather.weatherCode,
            isDay = weather.isDay == 1   // API 1/0 gönderiyor → true/false
        )
    }
}