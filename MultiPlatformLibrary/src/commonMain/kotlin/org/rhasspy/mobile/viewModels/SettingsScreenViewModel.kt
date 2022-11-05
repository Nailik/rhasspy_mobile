package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.settings.AppSettings

class SettingsScreenViewModel : ViewModel() {

    val currentLanguage = AppSettings.languageOption.data
    val currentTheme = AppSettings.themeOption.data
    val isBackgroundEnabled = AppSettings.isBackgroundServiceEnabled.data
    val isMicrophoneOverlayEnabled = AppSettings.isMicrophoneOverlayEnabled.data
    val isWakeWordSoundIndicationEnabled = AppSettings.isWakeWordSoundIndicationEnabled.data
    val isWakeWordLightIndicationEnabled = AppSettings.isWakeWordLightIndicationEnabled.data
    val isAutomaticSilenceDetectionEnabled = AppSettings.isAutomaticSilenceDetectionEnabled.data
    val logLevel = AppSettings.logLevel.data
    val isForceCancelEnabled = AppSettings.isForceCancelEnabled.data

}