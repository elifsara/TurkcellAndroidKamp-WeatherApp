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
    suspend fun searchCities(query: String): List<City> = coroutineScope {
        // 1) Önce şehirleri bul
        val cities = cityApi.searchCities(query).results.orEmpty()   // null ise boş liste

        // 2) Her şehrin hava durumunu AYNI ANDA iste
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
