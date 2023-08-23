package org.rhasspy.mobile.settings.settingsmigration

import kotlinx.collections.immutable.persistentListOf
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.data.theme.ThemeType
import org.rhasspy.mobile.platformspecific.utils.isDebug
import org.rhasspy.mobile.settings.settingsmigration.types.*

/**
 * directly consumed
 */
object NewAppSetting {

    val didShowCrashlyticsDialog = NewBooleanSetting(NewSettingsEnum.CrashlyticsDialog, false)
    val didShowChangelogDialog = NewIntSetting(NewSettingsEnum.ChangelogDialog, 0)

    val languageType = NewIOptionSetting(NewSettingsEnum.LanguageOption, LanguageType.English)
    val themeType = NewIOptionSetting(NewSettingsEnum.ThemeOption, ThemeType.System)

    val isAutomaticSilenceDetectionEnabled = NewBooleanSetting(NewSettingsEnum.AutomaticSilenceDetection, false)
    val automaticSilenceDetectionAudioLevel = NewFloatSetting(NewSettingsEnum.AutomaticSilenceDetectionAudioLevel, 40f)
    val automaticSilenceDetectionTime = NewLongNullableSetting(NewSettingsEnum.AutomaticSilenceDetectionTime, 2000)
    val automaticSilenceDetectionMinimumTime = NewLongNullableSetting(NewSettingsEnum.AutomaticSilenceDetectionMinimumTime, 2000)

    val isBackgroundServiceEnabled = NewBooleanSetting(NewSettingsEnum.BackgroundEnabled, false)
    val microphoneOverlaySizeOption = NewIOptionSetting(NewSettingsEnum.MicrophoneOverlaySize, MicrophoneOverlaySizeOption.Disabled)
    val isMicrophoneOverlayWhileAppEnabled = NewBooleanSetting(NewSettingsEnum.MicrophoneOverlayWhileApp, false)
    val microphoneOverlayPositionX = NewIntSetting(NewSettingsEnum.MicrophoneOverlayPositionX, 0)
    val microphoneOverlayPositionY = NewIntSetting(NewSettingsEnum.MicrophoneOverlayPositionY, 0)

    val isWakeWordDetectionTurnOnDisplayEnabled = NewBooleanSetting(NewSettingsEnum.BackgroundWakeWordDetectionTurnOnDisplay, false)
    val isSoundIndicationEnabled = NewBooleanSetting(NewSettingsEnum.SoundIndication, true)
    val soundIndicationOutputOption = NewIOptionSetting(NewSettingsEnum.SoundIndicationOutput, AudioOutputOption.Notification)
    val isWakeWordLightIndicationEnabled = NewBooleanSetting(NewSettingsEnum.WakeWordLightIndication, false)

    val isMqttApiDeviceChangeEnabled = NewBooleanSetting(NewSettingsEnum.MqttApiDeviceChangeEnabled, false)
    val isHttpApiDeviceChangeEnabled = NewBooleanSetting(NewSettingsEnum.HttpApiDeviceChangeEnabled, true)
    val volume = NewFloatSetting(NewSettingsEnum.Volume, 0.5F)
    val isHotWordEnabled = NewBooleanSetting(NewSettingsEnum.HotWordEnabled, true)
    val isAudioOutputEnabled = NewBooleanSetting(NewSettingsEnum.AudioOutputEnabled, true)
    val isIntentHandlingEnabled = NewBooleanSetting(NewSettingsEnum.IntentHandlingEnabled, true)

    val wakeSoundVolume = NewFloatSetting(NewSettingsEnum.WakeSoundVolume, 0.5F)
    val recordedSoundVolume = NewFloatSetting(NewSettingsEnum.RecordedSoundVolume, 0.5F)
    val errorSoundVolume = NewFloatSetting(NewSettingsEnum.ErrorSoundVolume, 0.5F)

    val wakeSound = NewStringSetting(NewSettingsEnum.WakeSound, SoundOption.Default.name)
    val recordedSound = NewStringSetting(NewSettingsEnum.RecordedSound, SoundOption.Default.name)
    val errorSound = NewStringSetting(NewSettingsEnum.ErrorSound, SoundOption.Default.name)

    //saves sound as pair, first is fileName as String, second is used and indicates if this custom sound file is used
    val customWakeSounds = NewStringListSetting(NewSettingsEnum.CustomWakeSounds, persistentListOf())
    val customRecordedSounds = NewStringListSetting(NewSettingsEnum.CustomRecordedSounds, persistentListOf())
    val customErrorSounds = NewStringListSetting(NewSettingsEnum.CustomErrorSounds, persistentListOf())

    val isCrashlyticsEnabled = NewBooleanSetting(NewSettingsEnum.Crashlytics, false)
    val isShowLogEnabled = NewBooleanSetting(NewSettingsEnum.ShowLog, isDebug())
    val isLogAudioFramesEnabled = NewBooleanSetting(NewSettingsEnum.LogAudioFrames, false)
    val logLevel = NewIOptionSetting(NewSettingsEnum.LogLevel, LogLevel.Debug)
    val isLogAutoscroll = NewBooleanSetting(NewSettingsEnum.LogAutoscroll, true)

    val audioFocusOption = NewIOptionSetting(NewSettingsEnum.AudioFocusOption, AudioFocusOption.Disabled)
    val isAudioFocusOnNotification = NewBooleanSetting(NewSettingsEnum.AudioFocusOnNotification, false)
    val isAudioFocusOnSound = NewBooleanSetting(NewSettingsEnum.AudioFocusOnSound, false)
    val isAudioFocusOnRecord = NewBooleanSetting(NewSettingsEnum.AudioFocusOnRecord, false)
    val isAudioFocusOnDialog = NewBooleanSetting(NewSettingsEnum.AudioFocusOnDialog, false)
    val isPauseRecordingOnMedia = NewBooleanSetting(NewSettingsEnum.AudioRecorderPauseRecordingOnMedia, true)

    val isDialogAutoscroll = NewBooleanSetting(NewSettingsEnum.DialogAutoScroll, true)
}