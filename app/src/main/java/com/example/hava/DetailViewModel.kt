package com.example.hava

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val forecast: ForecastResponse) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

// Detay ekranının ViewModel'i.
// Ekran döndürülünce DetailActivity ölüp yeniden doğar, ama bu nesne AYNI kalır →
// tahmin tekrar indirilmez. Activity gerçekten kapanınca (geri tuşu) onCleared() çağrılır.
class DetailViewModel(
    savedStateHandle: SavedStateHandle               // Activity'ye Intent ile gönderilen bilgiler burada
) : ViewModel() {

    private val repository = CityRepository()

    val city = City(
        id = savedStateHandle[DetailActivity.EXTRA_ID] ?: 0L,
        name = savedStateHandle[DetailActivity.EXTRA_NAME] ?: "",
        latitude = savedStateHandle[DetailActivity.EXTRA_LATITUDE] ?: 0.0,
        longitude = savedStateHandle[DetailActivity.EXTRA_LONGITUDE] ?: 0.0,
        country = savedStateHandle[DetailActivity.EXTRA_COUNTRY],
        admin1 = savedStateHandle[DetailActivity.EXTRA_ADMIN1]
    )

    var uiState by mutableStateOf<DetailUiState>(DetailUiState.Loading)
        private set

    // Bu şehir favorilerde mi? (veritabanından)
    var isFavorite by mutableStateOf(false)
        private set

    // Logcat'te yaşam döngüsünü izlemek için
    private val instanceNo = ++createdCount          // bu kaçıncı DetailViewModel?
    private var requestCount = 0                     // API'ye kaç kez gidildi?

    init {
        LifecycleLog.add("DetailViewModel #$instanceNo oluşturuldu")
        load()
        viewModelScope.launch {
            repository.favorites.collect { list -> isFavorite = list.any { it.id == city.id } }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            if (isFavorite) repository.removeFavorite(city.id) else repository.addFavorite(city)
        }
    }

    fun load() {
        viewModelScope.launch {
            uiState = DetailUiState.Loading
            requestCount++
            LifecycleLog.add("API isteği #$requestCount → ${city.name}")
            uiState = try {
                DetailUiState.Success(repository.getForecast(city.latitude, city.longitude))
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                DetailUiState.Error("İnternet bağlantını kontrol et.")
            } catch (e: Exception) {
                DetailUiState.Error("Bir hata oluştu: ${e.message}")
            }
        }
    }

    // Activity gerçekten bittiğinde (döndürmede DEĞİL) çağrılır
    override fun onCleared() {
        LifecycleLog.add("DetailViewModel #$instanceNo silindi (onCleared)")
    }

    companion object {
        private var createdCount = 0
    }
}
