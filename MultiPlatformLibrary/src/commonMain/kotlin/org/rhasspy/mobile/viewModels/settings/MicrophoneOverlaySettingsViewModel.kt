package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.settings.AppSettings

class MicrophoneOverlaySettingsViewModel : ViewModel() {

    //unsaved ui data
    val isMicrophoneOverlayEnabled = AppSettings.isMicrophoneOverlayEnabled.data
    val isMicrophoneOverlayWhileAppEnabledVisible = isMicrophoneOverlayEnabled
    val isMicrophoneOverlayWhileAppEnabled = AppSettings.isMicrophoneOverlayWhileAppEnabled.data

    //microphone overlay on/off
    fun toggleMicrophoneOverlayEnabled(enabled: Boolean) {
        AppSettings.isMicrophoneOverlayEnabled.value = enabled
    }

    //microphone overlay in background on/off
    fun toggleMicrophoneOverlayWhileAppEnabled(enabled: Boolean) {
        AppSettings.isMicrophoneOverlayWhileAppEnabled.value = enabled
    }

}