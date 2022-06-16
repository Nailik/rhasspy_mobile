package org.rhasspy.mobile.settings

import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.logger.LogLevel
import org.rhasspy.mobile.settings.sounds.SoundFile
import org.rhasspy.mobile.settings.sounds.SoundOptions

object AppSettings {

    val languageOption = AppSetting(SettingsEnum.LanguageOption, LanguageOptions.English)
    val themeOption = AppSetting(SettingsEnum.ThemeOption, ThemeOptions.System)

    val isAutomaticSilenceDetection = AppSetting(SettingsEnum.AutomaticSilenceDetection, false)
    val automaticSilenceDetectionAudioLevel = AppSetting(SettingsEnum.AutomaticSilenceDetectionAudioLevel, 40)
    val automaticSilenceDetectionTime = AppSetting(SettingsEnum.AutomaticSilenceDetectionTime, 2000)

    val isBackgroundEnabled = AppSetting(SettingsEnum.BackgroundEnabled, false)
    val isMicrophoneOverlayEnabled = AppSetting(SettingsEnum.MicrophoneOverlay, true)
    val isMicrophoneOverlayWhileApp = AppSetting(SettingsEnum.MicrophoneOverlayWhileApp, false)
    val isMicrophoneOverlayPositionX = AppSetting(SettingsEnum.MicrophoneOverlayPositionX, 0)
    val isMicrophoneOverlayPositionY = AppSetting(SettingsEnum.MicrophoneOverlayPositionY, 0)

    val isBackgroundWakeWordDetectionTurnOnDisplay = AppSetting(SettingsEnum.BackgroundWakeWordDetectionTurnOnDisplay, false)
    val isWakeWordSoundIndication = AppSetting(SettingsEnum.WakeWordSoundIndication, false)
    val isWakeWordLightIndication = AppSetting(SettingsEnum.WakeWordLightIndication, false)

    val volume = AppSetting(SettingsEnum.Volume, 0.5F)
    val isHotWordEnabled = AppSetting(SettingsEnum.HotWordEnabled, true)
    val isAudioOutputEnabled = AppSetting(SettingsEnum.AudioOutputEnabled, true)
    val isIntentHandlingEnabled = AppSetting(SettingsEnum.IntentHandlingEnabled, true)

    val soundVolume = AppSetting(SettingsEnum.SoundVolume, 0.5F)

    val wakeSound = AppSetting(SettingsEnum.WakeSound, SoundOptions.Default.name)
    val recordedSound = AppSetting(SettingsEnum.RecordedSound, SoundOptions.Default.name)
    val errorSound = AppSetting(SettingsEnum.ErrorSound, SoundOptions.Default.name)

    //saves sound as pair, first is fileName as String, second is used and indicates if this custom sound file is used
    val customSounds = AppSetting(SettingsEnum.CustomSounds, setOf<String>())

    val isShowLog = AppSetting(SettingsEnum.ShowLog, false)
    val isLogAudioFrames = AppSetting(SettingsEnum.LogAudioFrames, false)
    val logLevel = AppSetting(SettingsEnum.LogLevel, LogLevel.Debug)

}