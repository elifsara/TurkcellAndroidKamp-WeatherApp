package com.example.hava

// Ekran aynı anda bu 4 durumdan sadece birinde olabilir
sealed interface CityUiState {
    data object Loading : CityUiState                           // yükleniyor
    data class Success(val cities: List<City>) : CityUiState    // sonuç geldi
    data object Empty : CityUiState                             // istek başarılı ama sonuç yok
    data class Error(val message: String) : CityUiState         // hata oldu (ör. internet yok)
}
// "Konumum" kartının durumu
sealed interface LocationState {
    data object NotAsked : LocationState      // henüz izin istenmedi → "Konumumu kullan" düğmesi
    data object Loading : LocationState       // konum aranıyor
    data object Denied : LocationState        // kullanıcı izin vermedi
    data object Unavailable : LocationState   // izin var ama konum bulunamadı (GPS kapalı vb.)
    data class Found(val city: City) : LocationState
}
