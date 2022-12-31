package org.rhasspy.mobile.viewmodel.screens

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.settings.AppSettings

class SettingsScreenViewModel : ViewModel() {

    //TODO overlay permission for indication warning
    val currentLanguage = AppSettings.languageOption.data
    val isBackgroundEnabled = AppSettings.isBackgroundServiceEnabled.data
    val microphoneOverlaySizeOption = AppSettings.microphoneOverlaySizeOption.data
    val isSoundIndicationEnabled = AppSettings.isSoundIndicationEnabled.data
    val isWakeWordLightIndicationEnabled = AppSettings.isWakeWordLightIndicationEnabled.data
    val isAutomaticSilenceDetectionEnabled = AppSettings.isAutomaticSilenceDetectionEnabled.data
    val logLevel = AppSettings.logLevel.data

}