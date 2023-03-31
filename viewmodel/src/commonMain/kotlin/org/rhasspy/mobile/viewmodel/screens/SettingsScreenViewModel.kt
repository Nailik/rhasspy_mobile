package org.rhasspy.mobile.viewmodel.screens

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.logic.settings.AppSetting

class SettingsScreenViewModel : ViewModel() {

    val currentLanguage = AppSetting.languageType.data
    val isBackgroundEnabled = AppSetting.isBackgroundServiceEnabled.data
    val microphoneOverlaySizeOption = AppSetting.microphoneOverlaySizeOption.data
    val isSoundIndicationEnabled = AppSetting.isSoundIndicationEnabled.data
    val isWakeWordLightIndicationEnabled = AppSetting.isWakeWordLightIndicationEnabled.data
    val isAutomaticSilenceDetectionEnabled = AppSetting.isAutomaticSilenceDetectionEnabled.data
    val logLevel = AppSetting.logLevel.data

}