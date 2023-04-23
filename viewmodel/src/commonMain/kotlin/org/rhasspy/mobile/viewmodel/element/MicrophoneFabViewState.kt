package org.rhasspy.mobile.viewmodel.element

import androidx.compose.runtime.Stable
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
        isMicrophonePermissionAllowed: Boolean,
        dialogManagerServiceState: DialogManagerServiceState,
        isUserActionEnabled: Boolean,
        isShowBorder: Boolean,
        isShowMicOn: Boolean
    ) : this(
        isMicrophonePermissionRequired = !isMicrophonePermissionAllowed,
        dialogManagerServiceState = dialogManagerServiceState,
        isUserActionEnabled = isUserActionEnabled,
        isShowBorder = isShowBorder,
        isShowMicOn = isShowMicOn,
        isRecording = dialogManagerServiceState == DialogManagerServiceState.RecordingIntent
    )

}