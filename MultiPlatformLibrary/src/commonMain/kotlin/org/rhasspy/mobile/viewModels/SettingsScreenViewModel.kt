package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.AppSettings

class SettingsScreenViewModel : ViewModel() {

    val currentLanguage = AppSettings.languageOption.data
    val languageOptions = LanguageOptions::values

    fun selectLanguage(option: LanguageOptions) {
        AppSettings.languageOption.data.value = option
    }


    val currentTheme = AppSettings.themeOption.data
    val themeOptions = ThemeOptions::values

    fun selectTheme(option: ThemeOptions) {
        AppSettings.themeOption.data.value = option
    }

    val isBackgroundEnabled = AppSettings.isBackgroundServiceEnabled.data.readOnly
    val isMicrophoneOverlayWhileApp = AppSettings.isMicrophoneOverlayWhileAppEnabled.data.readOnly
    val isWakeWordSoundIndicationEnabled = AppSettings.isWakeWordSoundIndicationEnabled.data.readOnly
    val isWakeWordLightIndicationEnabled = AppSettings.isWakeWordLightIndicationEnabled.data.readOnly
    val isAutomaticSilenceDetectionEnabled = AppSettings.isAutomaticSilenceDetectionEnabled.data.readOnly
    val logLevel = AppSettings.logLevel.data.readOnly
    val isForceCancelEnabled = AppSettings.isForceCancelEnabled.data.readOnly

}