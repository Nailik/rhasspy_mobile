package org.rhasspy.mobile.widget.microphone

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabUiEvent.Action.MicrophoneFabClick
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewModel

/**
 * action when microphone widget is clicked
 */
class MicrophoneWidgetAction : ActionCallback, KoinComponent {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        if (get<MicrophonePermission>().granted.value) {
            get<MicrophoneFabViewModel>().onEvent(MicrophoneFabClick)
        } else {
            get<NativeApplication>().startRecordingAction()
        }
    }
}