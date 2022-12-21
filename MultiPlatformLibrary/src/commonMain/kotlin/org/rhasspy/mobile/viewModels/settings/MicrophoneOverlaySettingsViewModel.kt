package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.data.MicrophoneOverlaySizeOptions
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.settings.AppSettings

class MicrophoneOverlaySettingsViewModel : ViewModel() {

    //unsaved ui data
    val microphoneOverlaySizeOption = AppSettings.microphoneOverlaySizeOption.data
    val isMicrophoneOverlayWhileAppEnabledVisible =
        microphoneOverlaySizeOption.mapReadonlyState { it != MicrophoneOverlaySizeOptions.Disabled }
    val isMicrophoneOverlayWhileAppEnabled = AppSettings.isMicrophoneOverlayWhileAppEnabled.data
    val microphoneOverlaySizeOptions = MicrophoneOverlaySizeOptions::values

    //microphone overlay on/off
    fun selectMicrophoneOverlayOptionSize(option: MicrophoneOverlaySizeOptions) {
        AppSettings.microphoneOverlaySizeOption.value = option
    }

    //microphone overlay in background on/off
    fun toggleMicrophoneOverlayWhileAppEnabled(enabled: Boolean) {
        AppSettings.isMicrophoneOverlayWhileAppEnabled.value = enabled
    }

}