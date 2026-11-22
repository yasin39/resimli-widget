package com.yasin.resimliwidget.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.yasin.resimliwidget.work.DegistirWorker

class HayatWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = HayatWidget()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // KEEP politikası sayesinde tekrar çağrılması mevcut planı bozmaz.
        DegistirWorker.planla(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        DegistirWorker.iptalEt(context)
    }
}
