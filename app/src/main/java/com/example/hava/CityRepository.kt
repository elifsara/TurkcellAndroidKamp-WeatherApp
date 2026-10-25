package com.example.hava

// Verinin nereden geleceğine karar veren katman.
// ViewModel API'yi bilmez, sadece Repository'den şehir ister.
class CityRepository(
    private val api: CityApi = ApiClient.cityApi
) {
    suspend fun searchCities(query: String): List<City> {
        return api.searchCities(query).results.orEmpty()   // null ise boş liste
    }
}
