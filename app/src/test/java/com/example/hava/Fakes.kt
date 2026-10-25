package com.example.hava

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// Testlerde internete, veritabanına ve GPS'e gerçekten gitmiyoruz.
// Onların yerine istediğimiz cevabı veren sahte (fake) sınıflar kullanıyoruz.

class FakeCityApi(
    var results: List<City> = emptyList(),
    var error: Exception? = null           // doluysa API hata vermiş gibi davranır
) : CityApi {
    var callCount = 0                       // API kaç kez çağrıldı?

    override suspend fun searchCities(name: String, count: Int, language: String): CitySearchResponse {
        callCount++
        error?.let { throw it }
        return CitySearchResponse(results)
    }
}

class FakeWeatherApi : WeatherApi {
    override suspend fun getCurrentWeather(latitude: Double, longitude: Double, current: String) =
        WeatherResponse(CurrentWeather(temperature = 20.0, weatherCode = 0, isDay = 1))

    override suspend fun getForecast(
        latitude: Double, longitude: Double, current: String, hourly: String,
        daily: String, timezone: String, pastDays: Int, forecastDays: Int
    ): ForecastResponse = throw NotImplementedError()
}

// Veritabanı yerine bellekte duran bir liste
class FakeFavoriteDao : FavoriteDao {
    private val rows = MutableStateFlow<List<FavoriteCity>>(emptyList())

    override fun getAll(): Flow<List<FavoriteCity>> = rows

    override suspend fun insert(city: FavoriteCity) {
        rows.value = rows.value.filterNot { it.id == city.id } + city
    }

    override suspend fun delete(id: Long) {
        rows.value = rows.value.filterNot { it.id == id }
    }
}

class FakeLocationProvider(var city: City? = null) : LocationProvider {
    override suspend fun findMyCity(): City? = city
}

fun fakeRepository(
    cityApi: FakeCityApi = FakeCityApi(),
    favoriteDao: FakeFavoriteDao = FakeFavoriteDao(),
    locationProvider: FakeLocationProvider = FakeLocationProvider()
) = CityRepository(cityApi, FakeWeatherApi(), favoriteDao, locationProvider)

// ViewModel'ler viewModelScope ile "Main" thread'de çalışır. Testte Android olmadığı için
// Main yerine test dispatcher'ını koyuyoruz (her testten önce koy, sonra geri al).
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}
