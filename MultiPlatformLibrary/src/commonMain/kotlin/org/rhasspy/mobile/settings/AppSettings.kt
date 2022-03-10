package org.rhasspy.mobile.settings

import co.touchlab.kermit.Severity
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.logger.LogLevel

object AppSettings {

    val languageOption = AppSetting(SettingsEnum.LanguageOption, LanguageOptions.English)
    val themeOption = AppSetting(SettingsEnum.ThemeOption, ThemeOptions.System)

    val automaticSilenceDetection = AppSetting(SettingsEnum.AutomaticSilenceDetection, false)

    val isBackgroundEnabled = AppSetting(SettingsEnum.BackgroundEnabled, false)
    val isBackgroundWakeWordDetectionTurnOnDisplay = AppSetting(SettingsEnum.BackgroundWakeWordDetectionTurnOnDisplay, false)

    val isWakeWordSoundIndication = AppSetting(SettingsEnum.WakeWordSoundIndication, false)
    val isWakeWordLightIndication = AppSetting(SettingsEnum.WakeWordLightIndication, false)

    val isShowLog = AppSetting(SettingsEnum.ShowLog, false)
    val logLevel = AppSetting(SettingsEnum.LogLevel, LogLevel.Debug)

}