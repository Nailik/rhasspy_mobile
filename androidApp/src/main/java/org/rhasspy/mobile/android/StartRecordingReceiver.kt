package org.rhasspy.mobile.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabUiEvent.Action.UserSessionClick
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel

/**
 * Start Recording Receiver to not launch Main Activity when Permission is given
 */
class StartRecordingReceiver : KoinComponent, BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (get<MicrophonePermission>().granted.value) {
            get<MicrophoneFabViewModel>().onEvent(UserSessionClick)
        } else {
            get<NativeApplication>().startRecordingAction()
        }
    }
}