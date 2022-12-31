package org.rhasspy.mobile.viewmodel.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.settings.AppSettings

class DeviceSettingsSettingsViewModel : ViewModel() {

    //unsaved ui data
    val volume = AppSettings.volume.data
    val isHotWordEnabled = AppSettings.isHotWordEnabled.data
    val isAudioOutputEnabled = AppSettings.isAudioOutputEnabled.data
    val isIntentHandlingEnabled = AppSettings.isIntentHandlingEnabled.data

    //set new volume
    fun updateVolume(volume: Float) {
        AppSettings.volume.value = volume
    }

    //toggle hot word enabled
    fun toggleHotWordEnabled(enabled: Boolean) {
        AppSettings.isHotWordEnabled.value = enabled
    }

    //toggle audio output
    fun toggleAudioOutputEnabled(enabled: Boolean) {
        AppSettings.isAudioOutputEnabled.value = enabled
    }

    //toggle intent handling enabled
    fun toggleIntentHandlingEnabled(enabled: Boolean) {
        AppSettings.isIntentHandlingEnabled.value = enabled
    }

}