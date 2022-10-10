package org.rhasspy.mobile.settings

import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.logger.LogLevel
import org.rhasspy.mobile.settings.sounds.SoundOptions

internal object AppSettings {

    val languageOption = Setting(SettingsEnum.LanguageOption, LanguageOptions.English)
    val themeOption = Setting(SettingsEnum.ThemeOption, ThemeOptions.System)

    val isAutomaticSilenceDetectionEnabled = Setting(SettingsEnum.AutomaticSilenceDetection, false)
    val automaticSilenceDetectionAudioLevel = Setting(SettingsEnum.AutomaticSilenceDetectionAudioLevel, 40)
    val automaticSilenceDetectionTime = Setting(SettingsEnum.AutomaticSilenceDetectionTime, 2000)

    val isBackgroundEnabled = Setting(SettingsEnum.BackgroundEnabled, false)
    val isMicrophoneOverlayEnabled = Setting(SettingsEnum.MicrophoneOverlay, true)
    val isMicrophoneOverlayWhileApp = Setting(SettingsEnum.MicrophoneOverlayWhileApp, false)
    val microphoneOverlayPositionX = Setting(SettingsEnum.MicrophoneOverlayPositionX, 0)
    val microphoneOverlayPositionY = Setting(SettingsEnum.MicrophoneOverlayPositionY, 0)

    val isBackgroundWakeWordDetectionTurnOnDisplayEnabled = Setting(SettingsEnum.BackgroundWakeWordDetectionTurnOnDisplay, false)
    val isWakeWordSoundIndicationEnabled = Setting(SettingsEnum.WakeWordSoundIndication, false)
    val isWakeWordLightIndicationEnabled = Setting(SettingsEnum.WakeWordLightIndication, false)

    val volume = Setting(SettingsEnum.Volume, 0.5F)
    val isHotWordEnabled = Setting(SettingsEnum.HotWordEnabled, true)
    val isAudioOutputEnabled = Setting(SettingsEnum.AudioOutputEnabled, true)
    val isIntentHandlingEnabled = Setting(SettingsEnum.IntentHandlingEnabled, true)

    val soundVolume = Setting(SettingsEnum.SoundVolume, 0.5F)

    val wakeSound = Setting(SettingsEnum.WakeSound, SoundOptions.Default.name)
    val recordedSound = Setting(SettingsEnum.RecordedSound, SoundOptions.Default.name)
    val errorSound = Setting(SettingsEnum.ErrorSound, SoundOptions.Default.name)

    //saves sound as pair, first is fileName as String, second is used and indicates if this custom sound file is used
    val customSounds = Setting(SettingsEnum.CustomSounds, setOf<String>())

    val isShowLogEnabled = Setting(SettingsEnum.ShowLog, false)
    val isLogAudioFramesEnabled = Setting(SettingsEnum.LogAudioFrames, false)
    val logLevel = Setting(SettingsEnum.LogLevel, LogLevel.Debug)

    val isForceCancelEnabled = Setting(SettingsEnum.ForceCancel, false)

}