package org.rhasspy.mobile.settings

import kotlinx.collections.immutable.persistentListOf
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.platformspecific.language.getDeviceLanguage
import org.rhasspy.mobile.platformspecific.utils.isDebug
import org.rhasspy.mobile.settings.serializer.StringListSerializer

/**
 * directly consumed
 */
object AppSetting {

    val didShowCrashlyticsDialog = ISetting(SettingsEnum.CrashlyticsDialog, false)

    val languageType = ISetting(SettingsEnum.LanguageOption, getDeviceLanguage())

    val isAutomaticSilenceDetectionEnabled = ISetting(SettingsEnum.AutomaticSilenceDetection, false)
    val automaticSilenceDetectionAudioLevel =
        ISetting(SettingsEnum.AutomaticSilenceDetectionAudioLevel, 40f)
    val automaticSilenceDetectionTime = ISetting(SettingsEnum.AutomaticSilenceDetectionTime, 2000)
    val automaticSilenceDetectionMinimumTime = ISetting(SettingsEnum.AutomaticSilenceDetectionMinimumTime, 2000)

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
    val customWakeSounds = ISetting(SettingsEnum.CustomWakeSounds, persistentListOf(), StringListSerializer)
    val customRecordedSounds = ISetting(SettingsEnum.CustomRecordedSounds, persistentListOf(), StringListSerializer)
    val customErrorSounds = ISetting(SettingsEnum.CustomErrorSounds, persistentListOf(), StringListSerializer)

    val isCrashlyticsEnabled = ISetting(SettingsEnum.Crashlytics, false)
    val isShowLogEnabled = ISetting(SettingsEnum.ShowLog, isDebug())
    val isLogAudioFramesEnabled = ISetting(SettingsEnum.LogAudioFrames, false)
    val logLevel = ISetting(SettingsEnum.LogLevel, LogLevel.Debug)
    val isLogAutoscroll = ISetting(SettingsEnum.LogAutoscroll, true)

    val audioFocusOption = ISetting(SettingsEnum.AudioFocusOption, AudioFocusOption.Disabled)
    val isAudioFocusOnNotification = ISetting(SettingsEnum.AudioFocusOnNotification, false)
    val isAudioFocusOnSound = ISetting(SettingsEnum.AudioFocusOnSound, false)
    val isAudioFocusOnRecord = ISetting(SettingsEnum.AudioFocusOnRecord, false)
    val isAudioFocusOnDialog = ISetting(SettingsEnum.AudioFocusOnDialog, false)

    val audioRecorderChannel = ISetting(SettingsEnum.AudioRecorderChannel, AudioRecorderChannelType.default)
    val audioRecorderEncoding = ISetting(SettingsEnum.AudioRecorderEncoding, AudioRecorderEncodingType.default)
    val audioRecorderSampleRate = ISetting(SettingsEnum.AudioRecorderSampleRate, AudioRecorderSampleRateType.default)

}