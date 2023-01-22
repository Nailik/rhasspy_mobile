package org.rhasspy.mobile.settings

import org.rhasspy.mobile.Application
import org.rhasspy.mobile.logger.LogLevel
import org.rhasspy.mobile.settings.option.AudioOutputOption
import org.rhasspy.mobile.settings.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.settings.serializer.StringSetSerializer
import org.rhasspy.mobile.settings.sounds.SoundOption

/**
 * directly consumed
 */
object AppSetting {

    val languageType = ISetting(SettingsEnum.LanguageOption, Application.instance.getDeviceLanguage())

    val isAutomaticSilenceDetectionEnabled = ISetting(SettingsEnum.AutomaticSilenceDetection, false)
    val automaticSilenceDetectionAudioLevel =
        ISetting(SettingsEnum.AutomaticSilenceDetectionAudioLevel, 40f)
    val automaticSilenceDetectionTime = ISetting(SettingsEnum.AutomaticSilenceDetectionTime, 2000)

    val isBackgroundServiceEnabled = ISetting(SettingsEnum.BackgroundEnabled, false)
    val microphoneOverlaySizeOption =
        ISetting(SettingsEnum.MicrophoneOverlaySize, MicrophoneOverlaySizeOption.Disabled)
    val isMicrophoneOverlayWhileAppEnabled = ISetting(SettingsEnum.MicrophoneOverlayWhileApp, false)
    val microphoneOverlayPositionX = ISetting(SettingsEnum.MicrophoneOverlayPositionX, 0)
    val microphoneOverlayPositionY = ISetting(SettingsEnum.MicrophoneOverlayPositionY, 0)

    val isWakeWordDetectionTurnOnDisplayEnabled =
        ISetting(SettingsEnum.BackgroundWakeWordDetectionTurnOnDisplay, false)
    val isSoundIndicationEnabled = ISetting(SettingsEnum.SoundIndication, true)
    val soundIndicationOutputOption =
        ISetting(SettingsEnum.SoundIndicationOutput, AudioOutputOption.Notification)
    val isWakeWordLightIndicationEnabled = ISetting(SettingsEnum.WakeWordLightIndication, false)

    val volume = ISetting(SettingsEnum.Volume, 0.5F)
    val isHotWordEnabled = ISetting(SettingsEnum.HotWordEnabled, true)
    val isAudioOutputEnabled = ISetting(SettingsEnum.AudioOutputEnabled, true)
    val isIntentHandlingEnabled = ISetting(SettingsEnum.IntentHandlingEnabled, true)

    val wakeSoundVolume = ISetting(SettingsEnum.WakeSoundVolume, 0.5F)
    val recordedSoundVolume = ISetting(SettingsEnum.RecordedSoundVolume, 0.5F)
    val errorSoundVolume = ISetting(SettingsEnum.ErrorSoundVolume, 0.5F)

    val wakeSound = ISetting(SettingsEnum.WakeSound, SoundOption.Default.name)
    val recordedSound = ISetting(SettingsEnum.RecordedSound, SoundOption.Default.name)
    val errorSound = ISetting(SettingsEnum.ErrorSound, SoundOption.Default.name)

    //saves sound as pair, first is fileName as String, second is used and indicates if this custom sound file is used
    val customWakeSounds = ISetting(SettingsEnum.CustomWakeSounds, setOf(), StringSetSerializer)
    val customRecordedSounds =
        ISetting(SettingsEnum.CustomRecordedSounds, setOf(), StringSetSerializer)
    val customErrorSounds = ISetting(SettingsEnum.CustomErrorSounds, setOf(), StringSetSerializer)

    val isCrashlyticsEnabled = ISetting(SettingsEnum.Crashlytics, false)
    val isShowLogEnabled = ISetting(SettingsEnum.ShowLog, Application.instance.isDebug())
    val isLogAudioFramesEnabled = ISetting(SettingsEnum.LogAudioFrames, false)
    val logLevel = ISetting(SettingsEnum.LogLevel, LogLevel.Debug)
    val isLogAutoscroll = ISetting(SettingsEnum.LogAutoscroll, true)

}