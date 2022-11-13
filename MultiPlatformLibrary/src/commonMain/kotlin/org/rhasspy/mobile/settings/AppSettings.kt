package org.rhasspy.mobile.settings

import org.rhasspy.mobile.data.AudioOutputOptions
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.MicrophoneOverlaySizeOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.logger.LogLevel
import org.rhasspy.mobile.settings.serializer.StringSetSerializer
import org.rhasspy.mobile.settings.sounds.SoundOptions

internal object AppSettings {

    val languageOption = Setting(SettingsEnum.LanguageOption, LanguageOptions.English)
    val themeOption = Setting(SettingsEnum.ThemeOption, ThemeOptions.System)

    val isAutomaticSilenceDetectionEnabled = Setting(SettingsEnum.AutomaticSilenceDetection, false)
    val automaticSilenceDetectionAudioLevel = Setting(SettingsEnum.AutomaticSilenceDetectionAudioLevel, 40f)
    val automaticSilenceDetectionTime = Setting(SettingsEnum.AutomaticSilenceDetectionTime, 2000)

    val isBackgroundServiceEnabled = Setting(SettingsEnum.BackgroundEnabled, false)
    val microphoneOverlaySizeOption = Setting(SettingsEnum.MicrophoneOverlaySize, MicrophoneOverlaySizeOptions.Disabled)
    val isMicrophoneOverlayWhileAppEnabled = Setting(SettingsEnum.MicrophoneOverlayWhileApp, false)
    val microphoneOverlayPositionX = Setting(SettingsEnum.MicrophoneOverlayPositionX, 0)
    val microphoneOverlayPositionY = Setting(SettingsEnum.MicrophoneOverlayPositionY, 0)

    val isWakeWordDetectionTurnOnDisplayEnabled = Setting(SettingsEnum.BackgroundWakeWordDetectionTurnOnDisplay, false)
    val isSoundIndicationEnabled = Setting(SettingsEnum.SoundIndication, false)
    val soundIndicationOutputOption = Setting(SettingsEnum.SoundIndicationOutput, AudioOutputOptions.Notification)
    val isWakeWordLightIndicationEnabled = Setting(SettingsEnum.WakeWordLightIndication, false)

    val volume = Setting(SettingsEnum.Volume, 0.5F)
    val isHotWordEnabled = Setting(SettingsEnum.HotWordEnabled, true)
    val isAudioOutputEnabled = Setting(SettingsEnum.AudioOutputEnabled, true)
    val isIntentHandlingEnabled = Setting(SettingsEnum.IntentHandlingEnabled, true)

    val wakeSoundVolume = Setting(SettingsEnum.WakeSoundVolume, 0.5F)
    val recordedSoundVolume = Setting(SettingsEnum.RecordedSoundVolume, 0.5F)
    val errorSoundVolume = Setting(SettingsEnum.ErrorSoundVolume, 0.5F)

    val wakeSound = Setting(SettingsEnum.WakeSound, SoundOptions.Default.name)
    val recordedSound = Setting(SettingsEnum.RecordedSound, SoundOptions.Default.name)
    val errorSound = Setting(SettingsEnum.ErrorSound, SoundOptions.Default.name)

    //saves sound as pair, first is fileName as String, second is used and indicates if this custom sound file is used
    val customWakeSounds = Setting(SettingsEnum.CustomWakeSounds, setOf(), StringSetSerializer)
    val customRecordedSounds = Setting(SettingsEnum.CustomRecordedSounds, setOf(), StringSetSerializer)
    val customErrorSounds = Setting(SettingsEnum.CustomErrorSounds, setOf(), StringSetSerializer)

    val isShowLogEnabled = Setting(SettingsEnum.ShowLog, false)
    val isLogAudioFramesEnabled = Setting(SettingsEnum.LogAudioFrames, false)
    val logLevel = Setting(SettingsEnum.LogLevel, LogLevel.Debug)

    val isForceCancelEnabled = Setting(SettingsEnum.ForceCancel, false)

}