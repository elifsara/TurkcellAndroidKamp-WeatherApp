package com.example.hava

import android.app.Application

// Uygulama açılınca, tüm Activity'lerden önce bir kere oluşur.
// Veritabanını burada tek bir kez açıp herkese aynısını veriyoruz.
class HavaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.create(this)
    }

    companion object {
        lateinit var database: AppDatabase
            private set
    }
}
