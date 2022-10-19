package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.AppSettings

class MicrophoneOverlaySettingsViewModel : ViewModel() {

    //unsaved data
    private val _isMicrophoneOverlayEnabled = MutableStateFlow(AppSettings.isMicrophoneOverlayEnabled.value)
    private val _isMicrophoneOverlayWhileAppEnabled = MutableStateFlow(AppSettings.isMicrophoneOverlayWhileAppEnabled.value)

    //unsaved ui data
    val isMicrophoneOverlayEnabled = _isMicrophoneOverlayEnabled.readOnly
    val isMicrophoneOverlayWhileAppEnabled = _isMicrophoneOverlayWhileAppEnabled.readOnly

    //set new intent recognition option
    fun toggleMicrophoneOverlayEnabled(enabled: Boolean) {
        _isMicrophoneOverlayEnabled.value = enabled
    }

    fun toggleMicrophoneOverlayWhileAppEnabled(enabled: Boolean) {
        _isMicrophoneOverlayWhileAppEnabled.value = enabled
    }

    /**
     * save data configuration
     */
    fun save() {
        AppSettings.isMicrophoneOverlayEnabled.data.value = _isMicrophoneOverlayEnabled.value
        AppSettings.isMicrophoneOverlayWhileAppEnabled.data.value = _isMicrophoneOverlayWhileAppEnabled.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}