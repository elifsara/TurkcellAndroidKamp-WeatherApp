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
        val fromApi = cityApi.searchCities(query).results.orEmpty()   // null ise boş liste

        // API 1-2 harfte kısmi arama yapmıyor ("iz" → boş). Bu yüzden popüler şehirlerden
        // adı bu harflerle başlayanları da başa ekliyoruz (API'de zaten varsa tekrar eklemiyoruz).
        val fromDefaults = defaultCities
            .filter { it.name.startsWith(query, ignoreCase = true) }
            .filter { local -> fromApi.none { it.name.equals(local.name, ignoreCase = true) } }

        return withWeather(fromDefaults + fromApi)
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
