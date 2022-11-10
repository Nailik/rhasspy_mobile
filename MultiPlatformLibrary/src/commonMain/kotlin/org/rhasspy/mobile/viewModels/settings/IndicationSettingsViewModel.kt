package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.data.AudioOutputOptions
import org.rhasspy.mobile.settings.AppSettings


class IndicationSettingsViewModel : ViewModel() {

    //unsaved ui data
    val isSoundIndicationEnabled = AppSettings.isSoundIndicationEnabled.data
    val isWakeWordLightIndicationEnabled = AppSettings.isWakeWordLightIndicationEnabled.data
    val isWakeWordDetectionTurnOnDisplayEnabled = AppSettings.isWakeWordDetectionTurnOnDisplayEnabled.data
    val isSoundSettingsVisible = isSoundIndicationEnabled
    val soundIndicationOutputOption = AppSettings.soundIndicationOutputOption.data

    val wakeSound = AppSettings.wakeSound.data
    val recordedSound = AppSettings.recordedSound.data
    val errorSound = AppSettings.errorSound.data

    //all audio output options
    val audioOutputOptionsList = AudioOutputOptions::values

    //toggle wake word light indication
    fun toggleWakeWordLightIndicationEnabled(enabled: Boolean) {
        AppSettings.isWakeWordLightIndicationEnabled.value = enabled
    }

    //toggle wake word turn on display
    fun toggleWakeWordDetectionTurnOnDisplay(enabled: Boolean) {
        AppSettings.isWakeWordDetectionTurnOnDisplayEnabled.value = enabled
    }

    //toggle wake word sound indication
    fun toggleWakeWordSoundIndicationEnabled(enabled: Boolean) {
        AppSettings.isSoundIndicationEnabled.value = enabled
    }

    //set sound output option for indication
    fun selectSoundIndicationOutputOption(option: AudioOutputOptions) {
        AppSettings.soundIndicationOutputOption.value = option
    }

}