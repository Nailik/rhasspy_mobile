package org.rhasspy.mobile.viewmodel.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.settings.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting

class MicrophoneOverlaySettingsViewModel : ViewModel() {

    //unsaved ui data
    val microphoneOverlaySizeOption = AppSetting.microphoneOverlaySizeOption.data
    val isMicrophoneOverlayWhileAppEnabledVisible =
        microphoneOverlaySizeOption.mapReadonlyState { it != MicrophoneOverlaySizeOption.Disabled }
    val isMicrophoneOverlayWhileAppEnabled = AppSetting.isMicrophoneOverlayWhileAppEnabled.data
    val microphoneOverlaySizeOptions = MicrophoneOverlaySizeOption::values

    //microphone overlay on/off
    fun selectMicrophoneOverlayOptionSize(option: MicrophoneOverlaySizeOption) {
        AppSetting.microphoneOverlaySizeOption.value = option
    }

    //microphone overlay in background on/off
    fun toggleMicrophoneOverlayWhileAppEnabled(enabled: Boolean) {
        AppSetting.isMicrophoneOverlayWhileAppEnabled.value = enabled
    }

}