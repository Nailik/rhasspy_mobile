package org.rhasspy.mobile.viewmodel.element

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceState

@Stable
data class MicrophoneFabViewState internal constructor(
    val isMicrophonePermissionRequired: Boolean,
    val dialogManagerServiceState: DialogManagerServiceState,
    val isUserActionEnabled: Boolean,
    val isShowBorder: Boolean,
    val isShowMicOn: Boolean,
    val isRecording: Boolean
) {

    constructor(
        wakeWordOption: WakeWordOption,
        dialogManagerServiceState: DialogManagerServiceState,
        isUserActionEnabled: Boolean,
        isShowBorder: Boolean,
        isShowMicOn: Boolean,
    ) : this(
        isMicrophonePermissionRequired = wakeWordOption == WakeWordOption.Porcupine || wakeWordOption == WakeWordOption.Udp,
        dialogManagerServiceState = dialogManagerServiceState,
        isUserActionEnabled = isUserActionEnabled,
        isShowBorder = isShowBorder,
        isShowMicOn = isShowMicOn,
        isRecording = dialogManagerServiceState == DialogManagerServiceState.RecordingIntent
    )

}