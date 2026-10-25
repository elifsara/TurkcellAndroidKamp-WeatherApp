package com.example.hava

import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class CityViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `acilista populer sehirler gosterilir`() {
        val viewModel = CityViewModel(fakeRepository())

        val state = viewModel.uiState
        assertTrue(state is CityUiState.Success)
        assertEquals(defaultCities, (state as CityUiState.Success).cities)
    }

    @Test
    fun `internet yoksa hata mesaji gosterilir`() = runTest(mainDispatcherRule.dispatcher) {
        val api = FakeCityApi(error = IOException())
        val viewModel = CityViewModel(fakeRepository(cityApi = api))

        viewModel.onQueryChange("xyz")
        advanceTimeBy(500)   // 400 ms bekleme (debounce) bitsin

        assertEquals(CityUiState.Error("İnternet bağlantını kontrol et."), viewModel.uiState)
    }

    @Test
    fun `yazmaya devam ederken API'ye istek gitmez`() = runTest(mainDispatcherRule.dispatcher) {
        val api = FakeCityApi()
        val viewModel = CityViewModel(fakeRepository(cityApi = api))

        viewModel.onQueryChange("Ist")
        advanceTimeBy(200)
        viewModel.onQueryChange("Ista")
        advanceTimeBy(200)
        viewModel.onQueryChange("Istan")
        advanceTimeBy(500)

        assertEquals(1, api.callCount)   // sadece son metin aranır
    }

    @Test
    fun `kalbe basinca sehir favorilere eklenir ve tekrar basinca cikar`() {
        val viewModel = CityViewModel(fakeRepository())
        val ankara = turkishProvinces.first { it.name == "Ankara" }

        viewModel.toggleFavorite(ankara)
        assertTrue(viewModel.isFavorite(ankara))

        viewModel.toggleFavorite(ankara)
        assertTrue(viewModel.favorites.isEmpty())
    }

    @Test
    fun `konum izni reddedilince Denied durumuna gecer`() {
        val viewModel = CityViewModel(fakeRepository())

        viewModel.onLocationDenied()

        assertEquals(LocationState.Denied, viewModel.locationState)
    }

    @Test
    fun `konum bulununca Found durumuna gecer`() {
        val myCity = City(MY_LOCATION_ID, "Çankaya", 39.9, 32.86, null, null)
        val viewModel = CityViewModel(fakeRepository(locationProvider = FakeLocationProvider(myCity)))

        viewModel.loadMyLocation()

        assertEquals(LocationState.Found(myCity), viewModel.locationState)
    }
}
