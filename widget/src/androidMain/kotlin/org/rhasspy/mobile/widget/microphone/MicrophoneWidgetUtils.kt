package org.rhasspy.mobile.widget.microphone

import androidx.glance.appwidget.GlanceAppWidgetManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual object MicrophoneWidgetUtils : KoinComponent {

    actual suspend fun updateWidget() {
        val context = get<NativeApplication>()
        GlanceAppWidgetManager(context).getGlanceIds(MicrophoneWidget::class.java)
            .firstOrNull()
            ?.also { MicrophoneWidget().update(context, it) }
    }

}