package com.example.hava

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Activity ve ViewModel'lerin doğum/ölüm anlarını tutan küçük günlük.
// "object" olduğu için uygulama açık kaldıkça yaşar → Activity ölse bile kayıtlar kaybolmaz.
// Detay ekranındaki "Yaşam döngüsü" kartında ve Logcat'te ("tag:Yasam") görünür.
object LifecycleLog {

    val events = mutableStateListOf<String>()   // en yeni olay en üstte

    fun add(event: String) {
        Log.d("Yasam", event)
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        events.add(0, "$time  $event")
        if (events.size > 15) events.removeAt(events.lastIndex)   // sadece son 15 olay
    }
}
