package com.example.hava

import androidx.room.Entity
import androidx.room.PrimaryKey

// Veritabanındaki "favorite_cities" tablosunun bir satırı.
// Her alan tabloda bir sütun olur.
@Entity(tableName = "favorite_cities")
data class FavoriteCity(
    @PrimaryKey val id: Long,              // aynı şehir iki kez eklenemez
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?,
    val addedAt: Long = System.currentTimeMillis()   // ekleniş sırasına göre listelemek için
)

fun FavoriteCity.toCity() = City(id, name, latitude, longitude, country, admin1)

fun City.toFavorite() = FavoriteCity(id, name, latitude, longitude, country, admin1)
