package org.rhasspy.mobile.viewmodel.screens.home

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewState

@Stable
data class HomeScreenViewState internal constructor(
    val isMicrophonePermissionRequired: Boolean,
    val isPlayingRecording: Boolean,
    val isPlayingRecordingEnabled: Boolean,
    val microphoneFabViewState: MicrophoneFabViewState
) {

    constructor(
        wakeWordOption: WakeWordOption,
        isPlayingRecording: Boolean,
        isPlayingRecordingEnabled: Boolean,
        microphoneFabViewState: MicrophoneFabViewState
    ) : this(
        isMicrophonePermissionRequired = wakeWordOption == WakeWordOption.Porcupine || wakeWordOption == WakeWordOption.Udp,
        isPlayingRecording = isPlayingRecording,
        isPlayingRecordingEnabled = isPlayingRecordingEnabled,
        microphoneFabViewState = microphoneFabViewState
    )

}