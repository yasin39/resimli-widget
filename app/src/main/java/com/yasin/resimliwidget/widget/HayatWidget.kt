package com.yasin.resimliwidget.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import com.yasin.resimliwidget.data.Kart
import com.yasin.resimliwidget.data.KartDeposu
import com.yasin.resimliwidget.render.KartCizici

class HayatWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // İlk eklemede bir kart seçilmemişse rastgele birini kaydet.
        updateAppWidgetState(context, id) { durum ->
            if (KartDeposu.bul(context, durum[KART_ID]) == null) {
                durum[KART_ID] = KartDeposu.rastgele(context, null).id
            }
        }
        provideContent {
            val kartId = currentState<Preferences>()[KART_ID]
            val kart = KartDeposu.bul(context, kartId) ?: KartDeposu.rastgele(context, null)
            KartIcerigi(kart)
        }
    }

    @Composable
    private fun KartIcerigi(kart: Kart) {
        val context = LocalContext.current
        val boyut = LocalSize.current
        val yogunluk = context.resources.displayMetrics.density

        // Bitmap'i widget boyutunda çiz; bellek sınırı için uzun kenarı 1080 px ile sınırla.
        var genislik = (boyut.width.value * yogunluk).toInt().coerceAtLeast(1)
        var yukseklik = (boyut.height.value * yogunluk).toInt().coerceAtLeast(1)
        val oran = minOf(1f, EN_BUYUK_KENAR.toFloat() / maxOf(genislik, yukseklik))
        genislik = (genislik * oran).toInt().coerceAtLeast(1)
        yukseklik = (yukseklik * oran).toInt().coerceAtLeast(1)

        val bitmap = KartCizici.ciz(context, kart, genislik, yukseklik, KOSE_DP * yogunluk * oran)
        Image(
            provider = ImageProvider(bitmap),
            contentDescription = kart.baslik,
            contentScale = ContentScale.FillBounds,
            modifier = GlanceModifier.fillMaxSize().clickable(actionRunCallback<SonrakiKartAction>())
        )
    }

    companion object {
        val KART_ID = stringPreferencesKey("kart_id")
        private const val EN_BUYUK_KENAR = 1080
        private const val KOSE_DP = 20f
    }
}

/** Widget'a yeni rastgele bir kart atar ve yeniden çizer. */
suspend fun yeniKartGoster(context: Context, id: GlanceId) {
    updateAppWidgetState(context, id) { durum ->
        durum[HayatWidget.KART_ID] = KartDeposu.rastgele(context, durum[HayatWidget.KART_ID]).id
    }
    HayatWidget().update(context, id)
}
