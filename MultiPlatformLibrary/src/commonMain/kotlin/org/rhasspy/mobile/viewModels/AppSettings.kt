package org.rhasspy.mobile.viewModels

import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.settings.Setting
import org.rhasspy.mobile.settings.SettingsEnum

object AppSettings {

    val languageOption = Setting(SettingsEnum.LanguageOption, LanguageOptions.English)
    val themeOption = Setting(SettingsEnum.ThemeOption, ThemeOptions.System)

    val automaticSilenceDetection = Setting(SettingsEnum.AutomaticSilenceDetection, false)

    val isBackgroundWakeWordDetection = Setting(SettingsEnum.BackgroundWakeWordDetection, false)
    val isBackgroundWakeWordDetectionTurnOnDisplay = Setting(SettingsEnum.BackgroundWakeWordDetectionTurnOnDisplay, false)

    val isWakeWordSoundIndication = Setting(SettingsEnum.WakeWordSoundIndication, false)
    val isWakeWordLightIndication = Setting(SettingsEnum.WakeWordLightIndication, false)

    val isShowLog = Setting(SettingsEnum.ShowLog, false)

}