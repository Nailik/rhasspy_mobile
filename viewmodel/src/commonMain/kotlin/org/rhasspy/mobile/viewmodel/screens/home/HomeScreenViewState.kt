package org.rhasspy.mobile.viewmodel.screens.home

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewState

@Stable
data class HomeScreenViewState(
    val isMicrophonePermissionRequired: Boolean,
    val isPlayingRecording: Boolean,
    val isPlayingRecordingEnabled: Boolean,
    val microphoneFabViewState: MicrophoneFabViewState,
)