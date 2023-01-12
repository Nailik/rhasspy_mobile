package org.rhasspy.mobile.android.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel

/**
 * action when microphone widget is clicked
 */
class MicrophoneWidgetAction : ActionCallback, KoinComponent {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        if(MicrophonePermission.granted.value) {
            get<MicrophoneFabViewModel>().onClick()
        } else {
            MainActivity.startRecordingAction()
        }
    }
}