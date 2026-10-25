package com.example.hava

import android.app.Application

// Uygulama açılınca, tüm Activity'lerden önce bir kere oluşur.
// Veritabanını ve konum sağlayıcıyı burada tek bir kez oluşturup herkese aynısını veriyoruz.
class HavaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.create(this)
        locationProvider = DeviceLocationProvider(this)
    }

    companion object {
        lateinit var database: AppDatabase
            private set
        lateinit var locationProvider: LocationProvider
            private set
    }
}
