package org.rhasspy.mobile.viewmodel.microphone

import androidx.compose.runtime.Stable

@Stable
data class MicrophoneFabViewState(
    val isMicrophonePermissionAllowed: Boolean,
    val isUserActionEnabled: Boolean,
    val isShowBorder: Boolean,
    val isRecording: Boolean
)