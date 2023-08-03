package org.rhasspy.mobile.widget.microphone

import androidx.glance.appwidget.updateAll
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual object MicrophoneWidgetUtils : KoinComponent {

    actual suspend fun updateWidget() {
        val context = get<NativeApplication>()
        println("MicrophoneWidget updateAll")
        MicrophoneWidget().updateAll(context)
    }

}