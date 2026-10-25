package com.example.hava

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CityRepositoryTest {

    @Test
    fun `iki harfte 81 il icinde arar ve internete gitmez`() = runTest {
        val api = FakeCityApi()
        val repository = fakeRepository(cityApi = api)

        val result = repository.searchCities("es")

        assertEquals(listOf("Eskişehir"), result.map { it.name })
        assertEquals(0, api.callCount)
    }

    @Test
    fun `uc harf ve ustunde API sonuclarini da ekler ama ayni ili tekrar gostermez`() = runTest {
        val api = FakeCityApi(
            results = listOf(
                City(999, "Ankara", 0.0, 0.0, "Türkiye", "Ankara"),          // zaten 81 ilde var
                City(1000, "Ankaran", 45.5, 13.7, "Slovenya", "Koper")      // yeni
            )
        )
        val repository = fakeRepository(cityApi = api)

        val result = repository.searchCities("Ank")

        assertEquals(listOf("Ankara", "Ankaran"), result.map { it.name })
        assertEquals(1, api.callCount)
    }

    @Test
    fun `favori ekleyip silince liste guncellenir`() = runTest {
        val repository = fakeRepository()
        val izmir = turkishProvinces.first { it.name == "İzmir" }

        repository.addFavorite(izmir)
        assertEquals(listOf("İzmir"), repository.favorites.first().map { it.name })

        repository.removeFavorite(izmir.id)
        assertTrue(repository.favorites.first().isEmpty())
    }
}
