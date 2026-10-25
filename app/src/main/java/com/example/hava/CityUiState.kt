package com.example.hava

// Ekran aynı anda bu 4 durumdan sadece birinde olabilir
sealed interface CityUiState {
    data object Loading : CityUiState                           // yükleniyor
    data class Success(val cities: List<City>) : CityUiState    // sonuç geldi
    data object Empty : CityUiState                             // istek başarılı ama sonuç yok
    data class Error(val message: String) : CityUiState         // hata oldu (ör. internet yok)
}