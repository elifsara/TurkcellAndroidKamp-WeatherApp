package com.example.hava

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class CityViewModel : ViewModel() {

    // Şehirleri Repository'den alıyoruz (Ekran → ViewModel → Repository → API)
    private val repository = CityRepository()

    // Arama kutusundaki metin. Uygulama açılınca örnek olarak "Ankara" aransın.
    var query by mutableStateOf("Ankara")
        private set                       // dışarıdan sadece okunabilir, değiştirmek için fonksiyon

    // Ekranın şu anki durumu
    var uiState by mutableStateOf<CityUiState>(CityUiState.Loading)
        private set

    // Seçili şehrin id'si (artık ad yerine benzersiz id kullanıyoruz)
    var selectedCityId by mutableStateOf<Long?>(null)
        private set

    // Devam eden arama işi (yenisi başlayınca eskisini iptal edebilmek için)
    private var searchJob: Job? = null

    init {
        search()   // ViewModel ilk oluşturulduğunda bir kere ara
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
            if (text.length < 3) {                   // API 3 harften azında kısmi arama yapmıyor
                uiState = CityUiState.Empty
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
}