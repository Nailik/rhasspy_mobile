package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.AppSettings

class DeviceSettingsSettingsViewModel : ViewModel() {

    //unsaved data
    private val _volume = MutableStateFlow(AppSettings.volume.value)
    private val _isHotWordEnabled = MutableStateFlow(AppSettings.isHotWordEnabled.value)
    private val _isAudioOutputEnabled = MutableStateFlow(AppSettings.isAudioOutputEnabled.value)
    private val _isIntentHandlingEnabled = MutableStateFlow(AppSettings.isIntentHandlingEnabled.value)

    //unsaved ui data
    val volume = _volume.readOnly
    val isHotWordEnabled = _isHotWordEnabled.readOnly
    val isAudioOutputEnabled = _isAudioOutputEnabled.readOnly
    val isIntentHandlingEnabled = _isIntentHandlingEnabled.readOnly

    //set new volume
    fun updateVolume(volume: Float) {
        _volume.value = volume
    }

    //toggle hot word enabled
    fun toggleHotWordEnabled(enabled: Boolean) {
        _isHotWordEnabled.value = enabled
    }

    //toggle audio output
    fun toggleAudioOutputEnabled(enabled: Boolean) {
        _isAudioOutputEnabled.value = enabled
    }

    //toggle intent handling enabled
    fun toggleIntentHandlingEnabled(enabled: Boolean) {
        _isIntentHandlingEnabled.value = enabled
    }

    /**
     * save data configuration
     */
    fun save() {
        AppSettings.volume.value = _volume.value
        AppSettings.isHotWordEnabled.value = _isHotWordEnabled.value
        AppSettings.isAudioOutputEnabled.value = _isAudioOutputEnabled.value
        AppSettings.isIntentHandlingEnabled.value = _isIntentHandlingEnabled.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}