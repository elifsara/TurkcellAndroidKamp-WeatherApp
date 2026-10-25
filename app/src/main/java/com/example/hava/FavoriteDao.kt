package com.example.hava

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// DAO = veritabanına sorulacak soruların listesi. SQL'i biz yazarız, kodu Room üretir.
@Dao
interface FavoriteDao {

    // Flow: tablo her değiştiğinde yeni listeyi kendiliğinden gönderir → ekran otomatik güncellenir
    @Query("SELECT * FROM favorite_cities ORDER BY addedAt")
    fun getAll(): Flow<List<FavoriteCity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: FavoriteCity)

    @Query("DELETE FROM favorite_cities WHERE id = :id")
    suspend fun delete(id: Long)
}
