package com.example.mystudyapp

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class SettingsManager(context: Context) {

    // Satu objek SharedPreferences untuk seluruh aplikasi
    private val prefs = context.applicationContext
        .getSharedPreferences(NAMA_PREFS, Context.MODE_PRIVATE)

    companion object {
        private const val NAMA_PREFS = "pengaturan_app"

        // ===== kunci penyimpanan =====
        const val KEY_BAHASA     = "language"
        const val KEY_TEMA       = "theme"
        const val KEY_UKURAN     = "text_size"
        const val KEY_NOTIFIKASI = "notifikasi"
        const val KEY_AKSEN      = "accent"
        const val KEY_NAMA_PROFIL = "user_name"
        const val KEY_AVATAR     = "user_avatar"

        // ===== nilai yang sah =====
        const val BAHASA_ID   = "id"
        const val BAHASA_EN   = "en"

        const val TEMA_TERANG = "light"
        const val TEMA_GELAP  = "dark"
        const val TEMA_SISTEM = "system"

        const val UKURAN_KECIL  = "small"
        const val UKURAN_SEDANG = "medium"
        const val UKURAN_BESAR  = "large"

        const val AKSEN_HIJAU = "green"
        const val AKSEN_BIRU  = "blue"
        const val AKSEN_UNGU  = "purple"

        const val AVATAR_DEFAULT = "🎓"

        /**
         * Menerapkan tema & bahasa ke SELURUH aplikasi.
         * Dipanggil dari MyStudyApp dan setiap kali pengaturan diubah.
         */
        fun terapkanKeAplikasi(context: Context) {
            val s = SettingsManager(context)

            // 1) Tema: paksa mode terang, gelap, atau mengikuti sistem
            val modeMalam = when (s.ambilTema()) {
                TEMA_GELAP  -> AppCompatDelegate.MODE_NIGHT_YES
                TEMA_SISTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                else        -> AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(modeMalam)

            // 2) Bahasa: pakai file res/values atau res/values-en
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(s.ambilBahasa())
            )
        }
    }

    // ================= BAHASA =================
    fun ambilBahasa(): String = prefs.getString(KEY_BAHASA, BAHASA_ID) ?: BAHASA_ID
    fun simpanBahasa(nilai: String) = prefs.edit().putString(KEY_BAHASA, nilai).apply()

    // ================= TEMA =================
    fun ambilTema(): String = prefs.getString(KEY_TEMA, TEMA_TERANG) ?: TEMA_TERANG
    fun simpanTema(nilai: String) = prefs.edit().putString(KEY_TEMA, nilai).apply()

    // ================= UKURAN TEKS =================
    fun ambilUkuran(): String = prefs.getString(KEY_UKURAN, UKURAN_SEDANG) ?: UKURAN_SEDANG
    fun simpanUkuran(nilai: String) = prefs.edit().putString(KEY_UKURAN, nilai).apply()

    /** Ukuran teks halaman utama dalam satuan sp */
    fun ukuranTeksSp(): Float = when (ambilUkuran()) {
        UKURAN_KECIL -> 12f
        UKURAN_BESAR -> 17f
        else         -> 14f
    }

    // ================= WARNA AKSEN (Tambahan) =================
    fun ambilAksen(): String = prefs.getString(KEY_AKSEN, AKSEN_HIJAU) ?: AKSEN_HIJAU
    fun simpanAksen(nilai: String) = prefs.edit().putString(KEY_AKSEN, nilai).apply()

    fun warnaAksenInt(): Int = when (ambilAksen()) {
        AKSEN_BIRU -> Color.parseColor("#3B82F6")
        AKSEN_UNGU -> Color.parseColor("#A855F7")
        else       -> Color.parseColor("#3DDC84")
    }

    fun warnaLatarIkonInt(context: Context): Int {
        val malamAktif = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        return when (ambilAksen()) {
            AKSEN_BIRU -> Color.parseColor(if (malamAktif) "#1E293B" else "#EBF3FE")
            AKSEN_UNGU -> Color.parseColor(if (malamAktif) "#2E1F3B" else "#F3E8FF")
            else       -> Color.parseColor(if (malamAktif) "#1F3B2E" else "#E8F7EE")
        }
    }

    // ================= NOTIFIKASI =================
    fun ambilNotifikasi(): Boolean = prefs.getBoolean(KEY_NOTIFIKASI, false)
    fun simpanNotifikasi(nilai: Boolean) = prefs.edit().putBoolean(KEY_NOTIFIKASI, nilai).apply()

    // ================= PROFIL PENGGUNA (Tambahan) =================
    fun ambilNamaProfil(defaultNama: String): String =
        prefs.getString(KEY_NAMA_PROFIL, defaultNama) ?: defaultNama

    fun simpanNamaProfil(nama: String) =
        prefs.edit().putString(KEY_NAMA_PROFIL, nama).apply()

    fun ambilAvatar(): String =
        prefs.getString(KEY_AVATAR, AVATAR_DEFAULT) ?: AVATAR_DEFAULT

    fun simpanAvatar(avatar: String) =
        prefs.edit().putString(KEY_AVATAR, avatar).apply()

    // ================= RESET =================
    fun reset() = prefs.edit().clear().apply()
}

/**
 * Ringkasan pengaturan aktif — dipakai halaman utama DAN halaman pengaturan,
 * jadi tulisannya tidak perlu diulang di dua tempat.
 */
object RingkasanPengaturan {

    fun teks(context: Context, s: SettingsManager): String {
        val bahasa = context.getString(
            if (s.ambilBahasa() == SettingsManager.BAHASA_EN) R.string.singkat_en
            else R.string.singkat_id
        )
        val tema = context.getString(
            when (s.ambilTema()) {
                SettingsManager.TEMA_GELAP  -> R.string.singkat_gelap
                SettingsManager.TEMA_SISTEM -> R.string.singkat_sistem
                else                        -> R.string.singkat_terang
            }
        )
        val ukuran = context.getString(
            R.string.singkat_teks,
            context.getString(
                when (s.ambilUkuran()) {
                    SettingsManager.UKURAN_KECIL -> R.string.singkat_kecil
                    SettingsManager.UKURAN_BESAR -> R.string.singkat_besar
                    else                         -> R.string.singkat_sedang
                }
            )
        )
        val aksen = context.getString(
            when (s.ambilAksen()) {
                SettingsManager.AKSEN_BIRU -> R.string.singkat_aksen_biru
                SettingsManager.AKSEN_UNGU -> R.string.singkat_aksen_ungu
                else                       -> R.string.singkat_aksen_hijau
            }
        )
        return context.getString(R.string.ringkasan_format, bahasa, tema, ukuran, aksen)
    }

    fun statusNotifikasi(context: Context, s: SettingsManager): String = context.getString(
        R.string.status_format,
        context.getString(R.string.singkat_notifikasi),
        context.getString(
            if (s.ambilNotifikasi()) R.string.status_aktif else R.string.status_nonaktif
        )
    )
}
