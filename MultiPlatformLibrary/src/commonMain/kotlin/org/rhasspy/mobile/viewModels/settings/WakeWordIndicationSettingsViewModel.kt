package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.AppSettings

class WakeWordIndicationSettingsViewModel : ViewModel() {

    //unsaved data
    private val _isWakeWordSoundIndicationEnabled = MutableStateFlow(AppSettings.isWakeWordSoundIndicationEnabled.value)
    private val _isWakeWordLightIndicationEnabled = MutableStateFlow(AppSettings.isWakeWordLightIndicationEnabled.value)
    private val _isWakeWordDetectionTurnOnDisplayEnabled = MutableStateFlow(AppSettings.isWakeWordDetectionTurnOnDisplayEnabled.value)

    //unsaved ui data
    val isWakeWordSoundIndicationEnabled = _isWakeWordSoundIndicationEnabled.readOnly
    val isWakeWordLightIndicationEnabled = _isWakeWordLightIndicationEnabled.readOnly
    val isWakeWordDetectionTurnOnDisplayEnabled = _isWakeWordDetectionTurnOnDisplayEnabled.readOnly

    //toggle wake word sound indication
    fun toggleWakeWordSoundIndicationEnabled(enabled: Boolean) {
        _isWakeWordSoundIndicationEnabled.value = enabled
    }

    //toggle wake word light indication
    fun toggleWakeWordLightIndicationEnabled(enabled: Boolean) {
        _isWakeWordLightIndicationEnabled.value = enabled
    }

    //toggle wake word turn on display
    fun toggleWakeWordDetectionTurnOnDisplay(enabled: Boolean) {
        _isWakeWordDetectionTurnOnDisplayEnabled.value = enabled
    }

    /**
     * save data configuration
     */
    fun save() {
        AppSettings.isWakeWordSoundIndicationEnabled.value = _isWakeWordSoundIndicationEnabled.value
        AppSettings.isWakeWordLightIndicationEnabled.value = _isWakeWordLightIndicationEnabled.value
        AppSettings.isWakeWordDetectionTurnOnDisplayEnabled.value = _isWakeWordDetectionTurnOnDisplayEnabled.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}