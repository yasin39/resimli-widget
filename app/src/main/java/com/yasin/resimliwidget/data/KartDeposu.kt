package com.yasin.resimliwidget.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class IsimKaydi(
    val no: Int,
    val isim: String,
    val anlam: String,
    val gorsel: String
)

@Serializable
private data class BaslikKaydi(
    val no: Int,
    val baslik: String,
    val gorsel: String
)

object KartDeposu {

    private val json = Json { ignoreUnknownKeys = true }

    @Volatile
    private var kartlar: List<Kart>? = null

    fun tumKartlar(context: Context): List<Kart> {
        val mevcut = kartlar
        if (mevcut != null) {
            return mevcut
        }
        val yuklenen = yukle(context.applicationContext)
        kartlar = yuklenen
        return yuklenen
    }

    fun bul(context: Context, id: String?): Kart? {
        if (id == null) {
            return null
        }
        return tumKartlar(context).firstOrNull { it.id == id }
    }

    /** Rastgele bir kart seçer; bir önceki kart arka arkaya gelmez. */
    fun rastgele(context: Context, oncekiId: String?): Kart {
        val liste = tumKartlar(context)
        val adaylar = liste.filter { it.id != oncekiId }
        if (adaylar.isEmpty()) {
            return liste.random()
        }
        return adaylar.random()
    }

    private fun yukle(context: Context): List<Kart> {
        val isimler = json.decodeFromString<List<IsimKaydi>>(oku(context, "names.json")).map {
            Kart(
                id = "isim-${it.no}",
                tip = KartTipi.ISIM,
                baslik = it.isim,
                altyazi = it.anlam,
                gorsel = it.gorsel
            )
        }
        val basliklar = json.decodeFromString<List<BaslikKaydi>>(oku(context, "topics.json")).map {
            Kart(
                id = "baslik-${it.no}",
                tip = KartTipi.BASLIK,
                baslik = it.baslik,
                altyazi = null,
                gorsel = it.gorsel
            )
        }
        return isimler + basliklar
    }

    private fun oku(context: Context, dosya: String): String {
        return context.assets.open(dosya).bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
}
