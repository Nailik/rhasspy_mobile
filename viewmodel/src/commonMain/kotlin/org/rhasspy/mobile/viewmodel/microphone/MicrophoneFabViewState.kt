package org.rhasspy.mobile.viewmodel.microphone

import androidx.compose.runtime.Stable

@Stable
data class MicrophoneFabViewState internal constructor(
    val isMicrophonePermissionAllowed: Boolean,
    val isUserActionEnabled: Boolean,
    val isShowBorder: Boolean,
    val isRecording: Boolean
)