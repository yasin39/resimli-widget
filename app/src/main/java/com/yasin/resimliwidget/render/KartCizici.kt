package com.yasin.resimliwidget.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.yasin.resimliwidget.R
import com.yasin.resimliwidget.data.Kart
import com.yasin.resimliwidget.data.KartTipi

/** Görseli ve üzerindeki yazıyı tek bir bitmap'e çizer. */
object KartCizici {

    private const val ALTIN = 0xFFF2D184.toInt()
    private const val KREM = 0xFFFFF6E3.toInt()
    private const val GOLGE = 0xCC000000.toInt()

    fun ciz(context: Context, kart: Kart, genislik: Int, yukseklik: Int, koseYaricapi: Float): Bitmap {
        val bitmap = Bitmap.createBitmap(genislik, yukseklik, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val kose = Path()
        kose.addRoundRect(RectF(0f, 0f, genislik.toFloat(), yukseklik.toFloat()), koseYaricapi, koseYaricapi, Path.Direction.CW)
        canvas.clipPath(kose)

        arkaPlanCiz(context, canvas, kart, genislik, yukseklik)
        karartmaCiz(canvas, genislik, yukseklik)

        when (kart.tip) {
            KartTipi.ISIM -> isimYazisiCiz(canvas, kart, genislik, yukseklik)
            KartTipi.BASLIK -> baslikYazisiCiz(canvas, kart, genislik, yukseklik)
        }
        return bitmap
    }

    private fun arkaPlanCiz(context: Context, canvas: Canvas, kart: Kart, genislik: Int, yukseklik: Int) {
        val gorsel = gorselYukle(context, kart.gorselYolu)
        if (gorsel == null) {
            val yedek = ContextCompat.getDrawable(context, R.drawable.yedek_arka_plan)
            yedek?.setBounds(0, 0, genislik, yukseklik)
            yedek?.draw(canvas)
            return
        }
        // Ortadan kırparak alanı doldur (kare görsel kare olmayan widget'a sığsın).
        val oran = maxOf(genislik.toFloat() / gorsel.width, yukseklik.toFloat() / gorsel.height)
        val kaynakGenislik = (genislik / oran).toInt()
        val kaynakYukseklik = (yukseklik / oran).toInt()
        val sol = (gorsel.width - kaynakGenislik) / 2
        val ust = (gorsel.height - kaynakYukseklik) / 2
        val kaynak = Rect(sol, ust, sol + kaynakGenislik, ust + kaynakYukseklik)
        val hedef = Rect(0, 0, genislik, yukseklik)
        canvas.drawBitmap(gorsel, kaynak, hedef, Paint(Paint.FILTER_BITMAP_FLAG))
        gorsel.recycle()
    }

    private fun gorselYukle(context: Context, yol: String): Bitmap? {
        return try {
            context.assets.open(yol).use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) {
            null
        }
    }

    /** Yazının okunması için alt kısmı koyulaştırır. */
    private fun karartmaCiz(canvas: Canvas, genislik: Int, yukseklik: Int) {
        val baslangic = yukseklik * 0.40f
        val paint = Paint()
        paint.shader = LinearGradient(
            0f, baslangic, 0f, yukseklik.toFloat(),
            intArrayOf(Color.TRANSPARENT, 0x99000000.toInt(), 0xD9000000.toInt()),
            floatArrayOf(0f, 0.55f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, baslangic, genislik.toFloat(), yukseklik.toFloat(), paint)
    }

    private fun isimYazisiCiz(canvas: Canvas, kart: Kart, genislik: Int, yukseklik: Int) {
        val kisaKenar = minOf(genislik, yukseklik).toFloat()
        val kenarBosluk = genislik * 0.07f
        val yaziGenisligi = (genislik - kenarBosluk * 2).toInt()

        val isimBoya = yaziBoyasi(kisaKenar * 0.13f, ALTIN, Typeface.BOLD)
        isimBoya.letterSpacing = 0.06f
        val anlamBoya = yaziBoyasi(kisaKenar * 0.058f, KREM, Typeface.NORMAL)

        val isimDuzen = duzenOlustur(kart.baslik, isimBoya, yaziGenisligi, 1)
        val anlamDuzen = duzenOlustur(kart.altyazi ?: "", anlamBoya, yaziGenisligi, 3)

        val araBosluk = kisaKenar * 0.025f
        val altBosluk = yukseklik * 0.08f
        val anlamUst = yukseklik - altBosluk - anlamDuzen.height
        val isimUst = anlamUst - araBosluk - isimDuzen.height

        duzenCiz(canvas, isimDuzen, kenarBosluk, isimUst)
        duzenCiz(canvas, anlamDuzen, kenarBosluk, anlamUst)
    }

    private fun baslikYazisiCiz(canvas: Canvas, kart: Kart, genislik: Int, yukseklik: Int) {
        val kisaKenar = minOf(genislik, yukseklik).toFloat()
        val kenarBosluk = genislik * 0.08f
        val yaziGenisligi = (genislik - kenarBosluk * 2).toInt()

        val boya = yaziBoyasi(kisaKenar * 0.068f, ALTIN, Typeface.BOLD)
        val duzen = duzenOlustur(kart.baslik, boya, yaziGenisligi, 5)

        val altBosluk = yukseklik * 0.08f
        duzenCiz(canvas, duzen, kenarBosluk, yukseklik - altBosluk - duzen.height)
    }

    private fun yaziBoyasi(boyut: Float, renk: Int, stil: Int): TextPaint {
        val boya = TextPaint(Paint.ANTI_ALIAS_FLAG)
        boya.textSize = boyut
        boya.color = renk
        boya.typeface = Typeface.create(Typeface.SERIF, stil)
        boya.setShadowLayer(boyut * 0.12f, 0f, boyut * 0.05f, GOLGE)
        return boya
    }

    private fun duzenOlustur(metin: String, boya: TextPaint, genislik: Int, enFazlaSatir: Int): StaticLayout {
        return StaticLayout.Builder.obtain(metin, 0, metin.length, boya, genislik)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(0f, 1.1f)
            .setIncludePad(false)
            .setMaxLines(enFazlaSatir)
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()
    }

    private fun duzenCiz(canvas: Canvas, duzen: StaticLayout, x: Float, y: Float) {
        canvas.save()
        canvas.translate(x, y)
        duzen.draw(canvas)
        canvas.restore()
    }
}
