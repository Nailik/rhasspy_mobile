package org.rhasspy.mobile.widget.microphone

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.INativeApplication

actual object MicrophoneWidgetUtils : KoinComponent {

    actual suspend fun updateWidget() {
        val context = get<INativeApplication>()
        GlanceAppWidgetManager(context as Context).getGlanceIds(MicrophoneWidget::class.java)
            .firstOrNull()
            ?.also { MicrophoneWidget().update(context, it) }
    }

}