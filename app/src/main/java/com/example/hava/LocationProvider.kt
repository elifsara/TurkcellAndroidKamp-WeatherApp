package com.example.hava

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

// "Konumum" kartındaki şehrin id'si (gerçek şehirlerle karışmasın diye eksi)
const val MY_LOCATION_ID = -1L

// Bulunduğumuz yeri şehir olarak veren şey.
// Arayüz (interface) olduğu için testlerde telefonun GPS'i yerine sahte bir konum verebiliyoruz.
interface LocationProvider {
    suspend fun findMyCity(): City?   // konum bulunamazsa null
}

// Gerçek telefon konumu (LocationManager) + koordinattan şehir adı (Geocoder)
class DeviceLocationProvider(private val context: Context) : LocationProvider {

    @SuppressLint("MissingPermission")   // izin ekranda istendikten SONRA çağrılıyor
    override suspend fun findMyCity(): City? {
        val manager = context.getSystemService(LocationManager::class.java) ?: return null

        // Kullanılabilir ilk konum kaynağı (Android 12+'da "fused" hepsini birleştirir)
        val providers = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) add(LocationManager.FUSED_PROVIDER)
            add(LocationManager.GPS_PROVIDER)
            add(LocationManager.NETWORK_PROVIDER)
        }.filter { manager.isProviderEnabled(it) }
        if (providers.isEmpty()) return null   // konum servisleri kapalı

        // Önce taze konum iste (en fazla 10 sn), gelmezse telefonun bildiği son konumu kullan
        val location = withTimeoutOrNull(10_000) { currentLocation(manager, providers.first()) }
            ?: providers.firstNotNullOfOrNull { manager.getLastKnownLocation(it) }
            ?: return null

        return City(
            id = MY_LOCATION_ID,
            name = placeName(location) ?: "Bulunduğun yer",
            latitude = location.latitude,
            longitude = location.longitude,
            country = null,
            admin1 = "📍 Şu anki konumun"
        )
    }

    // Eski usul "sonuç gelince haber ver" (callback) fonksiyonunu suspend fonksiyona çeviriyoruz
    @SuppressLint("MissingPermission")
    private suspend fun currentLocation(manager: LocationManager, provider: String): Location? =
        suspendCancellableCoroutine { continuation ->
            val signal = CancellationSignal()
            continuation.invokeOnCancellation { signal.cancel() }
            LocationManagerCompat.getCurrentLocation(
                manager, provider, signal, ContextCompat.getMainExecutor(context)
            ) { location -> continuation.resume(location) }
        }

    // Koordinat → "Çankaya" gibi bir yer adı (internet gerekir; bulunamazsa null)
    private suspend fun placeName(location: Location): String? = withContext(Dispatchers.IO) {
        try {
            @Suppress("DEPRECATION")   // yeni sürümü sadece Android 13+'da var
            val address = Geocoder(context, Locale.forLanguageTag("tr"))
                .getFromLocation(location.latitude, location.longitude, 1)
                ?.firstOrNull()
            address?.subAdminArea ?: address?.locality ?: address?.adminArea
        } catch (e: Exception) {
            null
        }
    }
}
