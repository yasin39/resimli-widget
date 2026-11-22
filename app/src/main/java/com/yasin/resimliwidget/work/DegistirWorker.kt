package com.yasin.resimliwidget.work

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.yasin.resimliwidget.widget.HayatWidget
import com.yasin.resimliwidget.widget.yeniKartGoster
import java.util.concurrent.TimeUnit

/** Eklenmiş tüm widget'larda kartı yeniler. */
class DegistirWorker(context: Context, parametreler: WorkerParameters) : CoroutineWorker(context, parametreler) {

    override suspend fun doWork(): Result {
        val idler = GlanceAppWidgetManager(applicationContext).getGlanceIds(HayatWidget::class.java)
        for (id in idler) {
            yeniKartGoster(applicationContext, id)
        }
        return Result.success()
    }

    companion object {
        private const val IS_ADI = "kart_degistir"
        private const val ARALIK_DAKIKA = 30L

        fun planla(context: Context) {
            val istek = PeriodicWorkRequestBuilder<DegistirWorker>(ARALIK_DAKIKA, TimeUnit.MINUTES)
                .setInitialDelay(ARALIK_DAKIKA, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(IS_ADI, ExistingPeriodicWorkPolicy.KEEP, istek)
        }

        fun iptalEt(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(IS_ADI)
        }
    }
}
