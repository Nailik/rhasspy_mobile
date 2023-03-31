package org.rhasspy.mobile.viewmodel.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.data.service.option.AudioOutputOption


class IndicationSettingsViewModel : ViewModel() {

    //unsaved ui data
    val isSoundIndicationEnabled = AppSetting.isSoundIndicationEnabled.data
    val isWakeWordLightIndicationEnabled = AppSetting.isWakeWordLightIndicationEnabled.data
    val isWakeWordDetectionTurnOnDisplayEnabled =
        AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.data
    val isSoundSettingsVisible = isSoundIndicationEnabled
    val soundIndicationOutputOption = AppSetting.soundIndicationOutputOption.data

    val wakeSound = AppSetting.wakeSound.data
    val recordedSound = AppSetting.recordedSound.data
    val errorSound = AppSetting.errorSound.data

    //all audio output options
    val audioOutputOptionList = AudioOutputOption::values

    //toggle wake word light indication
    fun toggleWakeWordLightIndicationEnabled() {
        AppSetting.isWakeWordLightIndicationEnabled.value =
            !AppSetting.isWakeWordLightIndicationEnabled.value
    }

    //toggle wake word turn on display
    fun toggleWakeWordDetectionTurnOnDisplay(enabled: Boolean) {
        AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value = enabled
    }

    //toggle wake word sound indication
    fun toggleWakeWordSoundIndicationEnabled(enabled: Boolean) {
        AppSetting.isSoundIndicationEnabled.value = enabled
    }

    //set sound output option for indication
    fun selectSoundIndicationOutputOption(option: AudioOutputOption) {
        AppSetting.soundIndicationOutputOption.value = option
    }

}