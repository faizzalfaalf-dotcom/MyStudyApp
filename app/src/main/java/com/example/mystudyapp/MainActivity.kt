package com.example.mystudyapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var settings: SettingsManager

    // View yang ikut berubah ukuran teks, profil, dan warna aksennya
    private lateinit var tvNamaApp: TextView
    private lateinit var tvSubJudul: TextView
    private lateinit var tvAvatarUtama: TextView
    private lateinit var tvSapaanProfil: TextView
    private lateinit var btnProfil: Button
    private lateinit var tvIkonMateri: TextView
    private lateinit var tvIkonJadwal: TextView
    private lateinit var tvIkonTugas: TextView
    private lateinit var tvMateriIsi: TextView
    private lateinit var tvJadwalIsi: TextView
    private lateinit var tvTugasIsi: TextView
    private lateinit var tvRingkasan: TextView
    private lateinit var btnPengaturan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1) Siapkan pengelola pengaturan
        settings = SettingsManager(this)

        // 2) Ambil view
        tvNamaApp      = findViewById(R.id.tvNamaApp)
        tvSubJudul     = findViewById(R.id.tvSubJudul)
        tvAvatarUtama  = findViewById(R.id.tvAvatarUtama)
        tvSapaanProfil = findViewById(R.id.tvSapaanProfil)
        btnProfil      = findViewById(R.id.btnProfil)
        tvIkonMateri   = findViewById(R.id.tvIkonMateri)
        tvIkonJadwal   = findViewById(R.id.tvIkonJadwal)
        tvIkonTugas    = findViewById(R.id.tvIkonTugas)
        tvMateriIsi    = findViewById(R.id.tvMateriIsi)
        tvJadwalIsi    = findViewById(R.id.tvJadwalIsi)
        tvTugasIsi     = findViewById(R.id.tvTugasIsi)
        tvRingkasan    = findViewById(R.id.tvRingkasan)
        btnPengaturan  = findViewById(R.id.btnPengaturan)

        // 3) Tombol menuju halaman profil pengguna
        btnProfil.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }

        // 4) Tombol menuju halaman pengaturan
        btnPengaturan.setOnClickListener {
            startActivity(Intent(this, PengaturanActivity::class.java))
        }
    }

    /**
     * Dijalankan setiap kali halaman ini tampil kembali —
     * termasuk setelah kembali dari halaman pengaturan atau halaman profil.
     */
    override fun onResume() {
        super.onResume()
        terapkanPengaturanTampilan()
    }

    private fun terapkanPengaturanTampilan() {
        // 1) Terapkan ukuran teks pengaturan ke semua kalimat isi
        val ukuran = settings.ukuranTeksSp()
        listOf(tvSubJudul, tvMateriIsi, tvJadwalIsi, tvTugasIsi).forEach {
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, ukuran)
        }

        // 2) Tampilkan profil pengguna (nama & avatar)
        val nama = settings.ambilNamaProfil(getString(R.string.default_nama))
        tvSapaanProfil.text = getString(R.string.sapaan_profil, nama)
        tvAvatarUtama.text = settings.ambilAvatar()

        // 3) Terapkan warna aksen pilihan pengguna ke elemen utama
        val warnaAksen = settings.warnaAksenInt()
        val warnaLatarIkon = settings.warnaLatarIkonInt(this)

        tvNamaApp.setTextColor(warnaAksen)
        btnPengaturan.backgroundTintList = ColorStateList.valueOf(warnaAksen)
        btnPengaturan.setTextColor(Color.parseColor("#16213E"))

        btnProfil.backgroundTintList = ColorStateList.valueOf(warnaAksen)
        btnProfil.setTextColor(Color.parseColor("#16213E"))

        listOf(tvIkonMateri, tvIkonJadwal, tvIkonTugas).forEach {
            it.backgroundTintList = ColorStateList.valueOf(warnaLatarIkon)
        }

        // 4) Tampilkan ringkasan pengaturan yang sedang aktif
        tvRingkasan.text = RingkasanPengaturan.teks(this, settings)
    }
}
