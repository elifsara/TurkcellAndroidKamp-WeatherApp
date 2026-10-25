package com.example.hava

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Uygulamanın veritabanı (telefonda "hava.db" adlı bir dosya). İçinde tek tablo var: favoriler.
@Database(entities = [FavoriteCity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "hava.db").build()
    }
}
