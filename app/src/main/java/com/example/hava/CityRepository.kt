package com.example.hava

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

// Verinin nereden geleceğine karar veren katman.
// ViewModel API'yi bilmez, sadece Repository'den şehir ister.
class CityRepository(
    private val cityApi: CityApi = ApiClient.cityApi,
    private val weatherApi: WeatherApi = ApiClient.weatherApi
) {
    // Kutu boşken: sabit listeye sıcaklıkları ekle
    suspend fun getDefaultCities(): List<City> = withWeather(defaultCities)

    // Arama: önce şehirleri bul, sonra sıcaklıkları ekle
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

        return withWeather(fromProvinces + others)
    }

    // Her şehrin hava durumunu AYNI ANDA iste ve şehre ekle
    private suspend fun withWeather(cities: List<City>): List<City> = coroutineScope {
        cities.map { city ->
            async {
                val weather = try {
                    weatherApi.getCurrentWeather(city.latitude, city.longitude).current
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    null    // bir şehrin hava durumu gelmezse şehir yine görünsün, sadece "--°" yazsın
                }
                city.copy(
                    temperature = weather?.temperature,
                    weatherCode = weather?.weatherCode
                )
            }
        }.awaitAll()        // hepsinin bitmesini bekle
    }
}
