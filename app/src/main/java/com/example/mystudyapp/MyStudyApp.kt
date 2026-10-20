package com.example.mystudyapp

import android.app.Application

class MyStudyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Baca pengaturan terakhir dan terapkan ke seluruh aplikasi paling awal
        SettingsManager.terapkanKeAplikasi(this)
    }
}
