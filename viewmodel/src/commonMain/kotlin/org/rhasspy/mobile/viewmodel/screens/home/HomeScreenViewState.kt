package org.rhasspy.mobile.viewmodel.screens.home

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.service.option.WakeWordOption

@Stable
data class HomeScreenViewState internal constructor(
    val isMicrophonePermissionRequired: Boolean,
    val isPlayingRecording: Boolean,
    val isPlayingRecordingEnabled: Boolean,
    val isShowLogEnabled: Boolean
) {

    constructor(
        wakeWordOption: WakeWordOption,
        isPlayingRecording: Boolean,
        isPlayingRecordingEnabled: Boolean,
        isShowLogEnabled: Boolean
    ) : this(
        isMicrophonePermissionRequired = wakeWordOption == WakeWordOption.Porcupine || wakeWordOption == WakeWordOption.Udp,
        isPlayingRecording = isPlayingRecording,
        isPlayingRecordingEnabled = isPlayingRecordingEnabled,
        isShowLogEnabled = isShowLogEnabled
    )

}