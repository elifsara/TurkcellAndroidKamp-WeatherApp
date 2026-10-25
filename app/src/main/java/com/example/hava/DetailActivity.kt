package com.example.hava

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.hava.ui.theme.HavaTheme

// Şehir detayı için AYRI bir Activity (Navigation yerine bilerek Activity kullandık:
// iki Activity arasında geçerken yaşam döngüsünü Logcat'te "tag:Yasam" ile görebilmek için)
class DetailActivity : ComponentActivity() {

    private var activityNo = 0    // bu kaçıncı DetailActivity nesnesi?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNo = ++createdCount
        LifecycleLog.add("DetailActivity #$activityNo onCreate")

        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        setContent {
            HavaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DetailScreen(
                        onBack = { finish() }      // Activity'yi kapat → bir önceki ekrana dön
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LifecycleLog.add("DetailActivity #$activityNo onStart")
    }

    override fun onStop() {
        super.onStop()
        LifecycleLog.add("DetailActivity #$activityNo onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        // isChangingConfigurations: döndürme yüzünden mi ölüyor, yoksa gerçekten mi kapanıyor?
        val reason = if (isChangingConfigurations) "döndürme, yeniden doğacak" else "kapandı"
        LifecycleLog.add("DetailActivity #$activityNo onDestroy ($reason)")
    }

    companion object {
        private var createdCount = 0

        const val EXTRA_ID = "id"
        const val EXTRA_NAME = "name"
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
        const val EXTRA_COUNTRY = "country"
        const val EXTRA_ADMIN1 = "admin1"

        // Detay ekranını açmak için gereken Intent (şehir bilgileri "extra" olarak taşınır)
        fun newIntent(context: Context, city: City): Intent =
            Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_ID, city.id)
                putExtra(EXTRA_NAME, city.name)
                putExtra(EXTRA_LATITUDE, city.latitude)
                putExtra(EXTRA_LONGITUDE, city.longitude)
                putExtra(EXTRA_COUNTRY, city.country)
                putExtra(EXTRA_ADMIN1, city.admin1)
            }
    }
}
