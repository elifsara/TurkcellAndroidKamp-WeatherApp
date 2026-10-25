package com.example.hava

// Verinin nereden geleceğine karar veren katman.
// ViewModel API'yi bilmez, sadece Repository'den şehir ister.
class CityRepository(
    private val cityApi: CityApi = ApiClient.cityApi,
    private val weatherApi: WeatherApi = ApiClient.weatherApi
) {
    // Kutu boşken gösterilecek şehirler (telefonda hazır, internet gerekmez)
    fun getDefaultCities(): List<City> = defaultCities

    // Arama: sadece şehirleri bulur. Sıcaklıklar kart ekrana gelince ayrı ayrı istenir (getWeather).
    suspend fun searchCities(query: String): List<City> {
        // 1) Önce 81 il içinde ara (telefonda, internet gerekmez). "es" → Eskişehir
        val fromProvinces = turkishProvinces.filter { it.name.startsWith(query, ignoreCase = true) }

        // 2) API 1-2 harfte sonuç döndürmüyor; 3+ harfte dünyadaki diğer yerleri de ara
        val fromApi = if (query.length >= 3) {
            cityApi.searchCities(query).results.orEmpty()   // null ise boş liste
        } else {
            emptyList()
        }

        // API'den gelen ve zaten il listesinde olan şehri tekrar gösterme
        val others = fromApi.filterNot { api ->
            fromProvinces.any { it.name.equals(api.name, ignoreCase = true) }
        }

        return fromProvinces + others
    }

    // Tek bir şehrin anlık hava durumu (kart ekrana gelince çağrılır)
    suspend fun getWeather(city: City): CurrentWeather? =
        weatherApi.getCurrentWeather(city.latitude, city.longitude).current
}
