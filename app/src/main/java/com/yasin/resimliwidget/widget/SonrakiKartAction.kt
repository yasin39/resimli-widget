package com.yasin.resimliwidget.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

/** Widget'a dokununca sıradaki (rastgele) kartı gösterir. */
class SonrakiKartAction : ActionCallback {

    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        yeniKartGoster(context, glanceId)
    }
}
