package com.example.hava

import android.util.Log

// Activity ve ViewModel'lerin doğum/ölüm anlarını Logcat'e yazar.
// Android Studio → Logcat → arama kutusuna "tag:Yasam" yazarak izleyebilirsin.
object LifecycleLog {

    fun add(event: String) {
        Log.d("Yasam", event)
    }
}
