package org.rhasspy.mobile.viewmodel.microphone

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.SessionState.RecordingIntentState

@Stable
data class MicrophoneFabViewState internal constructor(
    val isMicrophonePermissionRequired: Boolean,
    val dialogManagerState: DialogManagerState,
    val isUserActionEnabled: Boolean,
    val isShowBorder: Boolean,
    val isShowMicOn: Boolean,
    val isRecording: Boolean
) {

    constructor(
        isMicrophonePermissionAllowed: Boolean,
        dialogManagerState: DialogManagerState,
        isUserActionEnabled: Boolean,
        isShowBorder: Boolean,
        isShowMicOn: Boolean
    ) : this(
        isMicrophonePermissionRequired = !isMicrophonePermissionAllowed,
        dialogManagerState = dialogManagerState,
        isUserActionEnabled = isUserActionEnabled,
        isShowBorder = isShowBorder,
        isShowMicOn = isShowMicOn,
        isRecording = dialogManagerState is RecordingIntentState
    )

}