package org.rhasspy.mobile.viewmodel.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.settings.AppSetting

class DeviceSettingsSettingsViewModel : ViewModel() {

    //unsaved ui data
    val volume = AppSetting.volume.data
    val isHotWordEnabled = AppSetting.isHotWordEnabled.data
    val isAudioOutputEnabled = AppSetting.isAudioOutputEnabled.data
    val isIntentHandlingEnabled = AppSetting.isIntentHandlingEnabled.data

    //set new volume
    fun updateVolume(volume: Float) {
        AppSetting.volume.value = volume
    }

    //toggle hot word enabled
    fun toggleHotWordEnabled(enabled: Boolean) {
        AppSetting.isHotWordEnabled.value = enabled
    }

    //toggle audio output
    fun toggleAudioOutputEnabled(enabled: Boolean) {
        AppSetting.isAudioOutputEnabled.value = enabled
    }

    //toggle intent handling enabled
    fun toggleIntentHandlingEnabled(enabled: Boolean) {
        AppSetting.isIntentHandlingEnabled.value = enabled
    }

}