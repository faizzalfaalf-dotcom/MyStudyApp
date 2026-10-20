package com.example.mystudyapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfilActivity : AppCompatActivity() {

    private lateinit var settings: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        settings = SettingsManager(this)

        val tvPreviewAvatar = findViewById<TextView>(R.id.tvPreviewAvatar)
        val rgAvatar        = findViewById<RadioGroup>(R.id.rgAvatar)
        val etNamaMahasiswa = findViewById<EditText>(R.id.etNamaMahasiswa)
        val btnSimpanProfil = findViewById<Button>(R.id.btnSimpanProfil)

        // Terapkan warna aksen pada tombol simpan
        btnSimpanProfil.backgroundTintList = ColorStateList.valueOf(settings.warnaAksenInt())
        btnSimpanProfil.setTextColor(Color.parseColor("#16213E"))

        // 1) Tampilkan keadaan awal profil
        val avatarTersimpan = settings.ambilAvatar()
        tvPreviewAvatar.text = avatarTersimpan
        etNamaMahasiswa.setText(settings.ambilNamaProfil(getString(R.string.default_nama)))

        rgAvatar.check(
            when (avatarTersimpan) {
                "💻" -> R.id.rbAvatar2
                "🚀" -> R.id.rbAvatar3
                "🐱" -> R.id.rbAvatar4
                else -> R.id.rbAvatar1
            }
        )

        // 2) Perbarui pratinjau saat radio avatar dipilih
        rgAvatar.setOnCheckedChangeListener { _, checkedId ->
            tvPreviewAvatar.text = when (checkedId) {
                R.id.rbAvatar2 -> "💻"
                R.id.rbAvatar3 -> "🚀"
                R.id.rbAvatar4 -> "🐱"
                else           -> "🎓"
            }
        }

        // 3) Simpan profil ke SharedPreferences lewat SettingsManager
        btnSimpanProfil.setOnClickListener {
            val namaInput = etNamaMahasiswa.text.toString().trim()
            if (namaInput.isEmpty()) {
                etNamaMahasiswa.error = getString(R.string.error_nama_kosong)
                return@setOnClickListener
            }

            settings.simpanNamaProfil(namaInput)
            settings.simpanAvatar(tvPreviewAvatar.text.toString())

            Toast.makeText(
                this,
                getString(R.string.toast_profil_disimpan),
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }
}
