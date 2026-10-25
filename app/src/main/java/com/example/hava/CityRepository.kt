package com.example.hava

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Verinin nereden geleceğine karar veren katman ("veri müdürü").
// ViewModel ne internetten ne veritabanından haberdar; sadece Repository'den ister.
//   - Şehir arama, hava durumu → internet (Retrofit)
//   - Favoriler              → telefondaki veritabanı (Room)
//   - 81 il, popüler şehirler → koda gömülü liste
class CityRepository(
    private val cityApi: CityApi = ApiClient.cityApi,
    private val weatherApi: WeatherApi = ApiClient.weatherApi,
    private val favoriteDao: FavoriteDao = HavaApp.database.favoriteDao()
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

    // Detay ekranı için ayrıntılı tahmin
    suspend fun getForecast(latitude: Double, longitude: Double): ForecastResponse =
        weatherApi.getForecast(latitude, longitude)

    // Favori şehirler; veritabanı her değiştiğinde yeni liste gelir
    val favorites: Flow<List<City>> =
        favoriteDao.getAll().map { list -> list.map { it.toCity() } }

    suspend fun addFavorite(city: City) = favoriteDao.insert(city.toFavorite())

    suspend fun removeFavorite(id: Long) = favoriteDao.delete(id)
}
