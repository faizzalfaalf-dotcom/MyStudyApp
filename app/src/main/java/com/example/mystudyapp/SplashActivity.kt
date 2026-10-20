package com.example.mystudyapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val settings = SettingsManager(this)

        val tvSplashLogo = findViewById<TextView>(R.id.tvSplashLogo)
        val tvSplashNama = findViewById<TextView>(R.id.tvSplashNama)

        tvSplashLogo.text = settings.ambilAvatar()
        tvSplashNama.setTextColor(settings.warnaAksenInt())

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1200)
    }
}
