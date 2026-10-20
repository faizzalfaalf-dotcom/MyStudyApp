package com.example.mystudyapp

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class PengaturanActivity : AppCompatActivity() {

    private lateinit var settings: SettingsManager
    private lateinit var tvRingkasan: TextView
    private lateinit var tvStatusNotifikasi: TextView

    // Penanda agar perubahan view secara programatis / pemulihan state tidak memicu listener
    private var sedangMemuatData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaturan)

        settings = SettingsManager(this)

        tvRingkasan = findViewById(R.id.tvRingkasan)
        tvStatusNotifikasi = findViewById(R.id.tvStatusNotifikasi)

        val rgBahasa     = findViewById<RadioGroup>(R.id.rgBahasa)
        val rgTema       = findViewById<RadioGroup>(R.id.rgTema)
        val rgUkuran     = findViewById<RadioGroup>(R.id.rgUkuran)
        val rgAksen      = findViewById<RadioGroup>(R.id.rgAksen)
        val swNotifikasi = findViewById<SwitchCompat>(R.id.swNotifikasi)

        // 1) TAMPILKAN KEADAAN AWAL dari pengaturan yang tersimpan
        isiKeadaanAwal(rgBahasa, rgTema, rgUkuran, rgAksen, swNotifikasi)

        // 2) BARU pasang listener: setiap perubahan disimpan & diterapkan jika bukan dari pemuatan data
        rgBahasa.setOnCheckedChangeListener { _, id ->
            if (sedangMemuatData) return@setOnCheckedChangeListener

            val bahasaBaru = if (id == R.id.rbEnglish) SettingsManager.BAHASA_EN
                             else SettingsManager.BAHASA_ID
            val bahasaLama = settings.ambilBahasa()

            if (bahasaBaru == bahasaLama) return@setOnCheckedChangeListener

            // Konfirmasi sebelum mengubah pengaturan bahasa
            konfirmasiUbahBahasa(rgBahasa, bahasaLama, bahasaBaru)
        }

        rgTema.setOnCheckedChangeListener { _, id ->
            if (sedangMemuatData) return@setOnCheckedChangeListener

            val nilaiTema = when (id) {
                R.id.rbGelap  -> SettingsManager.TEMA_GELAP
                R.id.rbSistem -> SettingsManager.TEMA_SISTEM
                else          -> SettingsManager.TEMA_TERANG
            }
            settings.simpanTema(nilaiTema)
            SettingsManager.terapkanKeAplikasi(this) // tampilan langsung berganti tema
        }

        rgUkuran.setOnCheckedChangeListener { _, id ->
            if (sedangMemuatData) return@setOnCheckedChangeListener

            val nilaiUkuran = when (id) {
                R.id.rbKecil -> SettingsManager.UKURAN_KECIL
                R.id.rbBesar -> SettingsManager.UKURAN_BESAR
                else         -> SettingsManager.UKURAN_SEDANG
            }
            settings.simpanUkuran(nilaiUkuran)
            perbaruiRingkasan()
        }

        rgAksen.setOnCheckedChangeListener { _, id ->
            if (sedangMemuatData) return@setOnCheckedChangeListener

            val nilaiAksen = when (id) {
                R.id.rbAksenBiru -> SettingsManager.AKSEN_BIRU
                R.id.rbAksenUngu -> SettingsManager.AKSEN_UNGU
                else             -> SettingsManager.AKSEN_HIJAU
            }
            settings.simpanAksen(nilaiAksen)
            perbaruiRingkasan()
        }

        swNotifikasi.setOnCheckedChangeListener { _, aktif ->
            if (sedangMemuatData) return@setOnCheckedChangeListener

            settings.simpanNotifikasi(aktif)
            perbaruiRingkasan()
        }

        findViewById<Button>(R.id.btnReset).setOnClickListener { konfirmasiReset() }
    }

    override fun onResume() {
        super.onResume()
        perbaruiRingkasan()
    }

    // ================= keadaan awal =================
    private fun isiKeadaanAwal(
        rgBahasa: RadioGroup,
        rgTema: RadioGroup,
        rgUkuran: RadioGroup,
        rgAksen: RadioGroup,
        swNotifikasi: SwitchCompat
    ) {
        sedangMemuatData = true
        try {
            rgBahasa.check(
                if (settings.ambilBahasa() == SettingsManager.BAHASA_EN) R.id.rbEnglish
                else R.id.rbIndonesia
            )
            rgTema.check(
                when (settings.ambilTema()) {
                    SettingsManager.TEMA_GELAP  -> R.id.rbGelap
                    SettingsManager.TEMA_SISTEM -> R.id.rbSistem
                    else                        -> R.id.rbTerang
                }
            )
            rgUkuran.check(
                when (settings.ambilUkuran()) {
                    SettingsManager.UKURAN_KECIL -> R.id.rbKecil
                    SettingsManager.UKURAN_BESAR -> R.id.rbBesar
                    else                         -> R.id.rbSedang
                }
            )
            rgAksen.check(
                when (settings.ambilAksen()) {
                    SettingsManager.AKSEN_BIRU -> R.id.rbAksenBiru
                    SettingsManager.AKSEN_UNGU -> R.id.rbAksenUngu
                    else                       -> R.id.rbAksenHijau
                }
            )
            swNotifikasi.isChecked = settings.ambilNotifikasi()
            perbaruiRingkasan()
        } finally {
            sedangMemuatData = false
        }
    }

    // ================= ringkasan pengaturan aktif =================
    private fun perbaruiRingkasan() {
        tvRingkasan.text = RingkasanPengaturan.teks(this, settings)
        tvStatusNotifikasi.text = RingkasanPengaturan.statusNotifikasi(this, settings)
    }

    // ================= konfirmasi ubah bahasa =================
    private fun konfirmasiUbahBahasa(
        rgBahasa: RadioGroup,
        bahasaLama: String,
        bahasaBaru: String
    ) {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_bahasa_judul)
            .setMessage(R.string.dialog_bahasa_pesan)
            .setCancelable(false)
            .setNegativeButton(R.string.dialog_batal) { _, _ ->
                // Kembalikan RadioButton ke bahasa sebelumnya tanpa memicu ulang dialog
                sedangMemuatData = true
                rgBahasa.check(
                    if (bahasaLama == SettingsManager.BAHASA_EN) R.id.rbEnglish
                    else R.id.rbIndonesia
                )
                sedangMemuatData = false
            }
            .setPositiveButton(R.string.dialog_ya_ubah) { _, _ ->
                settings.simpanBahasa(bahasaBaru)
                SettingsManager.terapkanKeAplikasi(this)
            }
            .show()
    }

    // ================= reset 2 tahap (1. Bahasa -> 2. Tampilan, Ukuran, Aksen, Notif) =================
    private fun konfirmasiReset() {
        // Alert 1: Bahasa
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_reset_bahasa_judul)
            .setMessage(R.string.dialog_reset_bahasa_pesan)
            .setNegativeButton(R.string.dialog_batal, null)
            .setPositiveButton(R.string.dialog_lanjut) { _, _ ->
                // Reset Bahasa ke Bahasa Indonesia
                settings.simpanBahasa(SettingsManager.BAHASA_ID)

                // Lanjut ke Alert 2 (Tampilan & Notifikasi)
                tampilkanAlertResetLainnya()
            }
            .show()
    }

    private fun tampilkanAlertResetLainnya() {
        // Alert 2: Tampilan, Ukuran Teks, Warna Aksen, dan Notifikasi
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_reset_lainnya_judul)
            .setMessage(R.string.dialog_reset_lainnya_pesan)
            .setNegativeButton(R.string.dialog_batal) { _, _ ->
                // Jika reset tampilan dibatalkan, terapkan reset bahasa yang sudah dipilih
                selesaikanReset()
            }
            .setPositiveButton(R.string.dialog_ya) { _, _ ->
                // Reset Tampilan, Ukuran Teks, Warna Aksen, Notifikasi
                settings.simpanTema(SettingsManager.TEMA_TERANG)
                settings.simpanUkuran(SettingsManager.UKURAN_SEDANG)
                settings.simpanAksen(SettingsManager.AKSEN_HIJAU)
                settings.simpanNotifikasi(false)

                selesaikanReset()
            }
            .show()
    }

    private fun selesaikanReset() {
        Toast.makeText(
            this,
            getString(R.string.toast_reset),
            Toast.LENGTH_SHORT
        ).show()

        // Terapkan tema dan bahasa default ke seluruh aplikasi
        SettingsManager.terapkanKeAplikasi(this)

        // Restart activity secara bersih tanpa menyimpan instance state lama
        finish()
        startActivity(intent)
    }
}

