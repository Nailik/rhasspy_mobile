package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.settings.AppSettings

class SettingsScreenViewModel : ViewModel() {

    val currentLanguage = AppSettings.languageOption.data
    val currentTheme = AppSettings.themeOption.data
    val isBackgroundEnabled = AppSettings.isBackgroundServiceEnabled.data
    val microphoneOverlaySizeOption = AppSettings.microphoneOverlaySizeOption.data
    val isSoundIndicationEnabled = AppSettings.isSoundIndicationEnabled.data
    val isWakeWordLightIndicationEnabled = AppSettings.isWakeWordLightIndicationEnabled.data
    val isAutomaticSilenceDetectionEnabled = AppSettings.isAutomaticSilenceDetectionEnabled.data
    val logLevel = AppSettings.logLevel.data
    val isForceCancelEnabled = AppSettings.isForceCancelEnabled.data

}