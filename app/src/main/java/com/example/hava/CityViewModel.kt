package com.example.hava

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.IOException

class CityViewModel : ViewModel() {

    // Arama kutusundaki metin. Uygulama açılınca örnek olarak "Ankara" aransın.
    var query by mutableStateOf("Ankara")
        private set                       // dışarıdan sadece okunabilir, değiştirmek için fonksiyon

    // Ekranın şu anki durumu
    var uiState by mutableStateOf<CityUiState>(CityUiState.Loading)
        private set

    // Seçili şehrin id'si (artık ad yerine benzersiz id kullanıyoruz)
    var selectedCityId by mutableStateOf<Long?>(null)
        private set

    init {
        search()   // ViewModel ilk oluşturulduğunda bir kere ara
    }

    fun onQueryChange(newQuery: String){
        query = newQuery
    }

    fun onCityClick(city: City) {
        selectedCityId = city.id
        Log.d("HavaApp", "Tıklandı: ${city.name}")
    }

    fun search() {
        viewModelScope.launch {     // arka planda bir iş başlat
            uiState = CityUiState.Loading
            uiState = try {
                val response = ApiClient.cityApi.searchCities(query.trim())
                val cities = response.results.orEmpty()  // null ise boş liste
                if (cities.isEmpty()){
                    CityUiState.Empty
                } else {
                    CityUiState.Success(cities)
                }
            } catch (e: IOException) {                   // internet yok, zaman aşımı..
                CityUiState.Error("İnternet bağlantını kontrol et.")
            } catch (e: Exception) {                  // diğer her şey
                CityUiState.Error("Bir hata oluştu: ${e.message}")
            }
        }
    }
}