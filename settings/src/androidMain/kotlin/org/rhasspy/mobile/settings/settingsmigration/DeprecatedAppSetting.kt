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

/**
 * directly consumed
 */
object DeprecatedAppSetting {

    val didShowCrashlyticsDialog = DeprecatedISetting(DeprecatedSettingsEnum.CrashlyticsDialog, false)
    val didShowChangelogDialog = DeprecatedISetting(DeprecatedSettingsEnum.ChangelogDialog, 0)

    val languageType = DeprecatedISetting(DeprecatedSettingsEnum.LanguageOption, LanguageType.English)
    val themeType = DeprecatedISetting(DeprecatedSettingsEnum.ThemeOption, ThemeType.System)

    val automaticSilenceDetectionAudioLevel = DeprecatedISetting(DeprecatedSettingsEnum.AutomaticSilenceDetectionAudioLevel, 40f)
    val automaticSilenceDetectionTime = DeprecatedISetting<Long?>(DeprecatedSettingsEnum.AutomaticSilenceDetectionTime, 2000)
    val automaticSilenceDetectionMinimumTime = DeprecatedISetting<Long?>(DeprecatedSettingsEnum.AutomaticSilenceDetectionMinimumTime, 2000)

    val isBackgroundServiceEnabled = DeprecatedISetting(DeprecatedSettingsEnum.BackgroundEnabled, false)
    val microphoneOverlaySizeOption = DeprecatedISetting(DeprecatedSettingsEnum.MicrophoneOverlaySize, MicrophoneOverlaySizeOption.Disabled)
    val isMicrophoneOverlayWhileAppEnabled = DeprecatedISetting(DeprecatedSettingsEnum.MicrophoneOverlayWhileApp, false)
    val microphoneOverlayPositionX = DeprecatedISetting(DeprecatedSettingsEnum.MicrophoneOverlayPositionX, 0)
    val microphoneOverlayPositionY = DeprecatedISetting(DeprecatedSettingsEnum.MicrophoneOverlayPositionY, 0)

    val isWakeWordDetectionTurnOnDisplayEnabled = DeprecatedISetting(DeprecatedSettingsEnum.BackgroundWakeWordDetectionTurnOnDisplay, false)
    val isSoundIndicationEnabled = DeprecatedISetting(DeprecatedSettingsEnum.SoundIndication, true)
    val soundIndicationOutputOption = DeprecatedISetting(DeprecatedSettingsEnum.SoundIndicationOutput, AudioOutputOption.Notification)
    val isWakeWordLightIndicationEnabled = DeprecatedISetting(DeprecatedSettingsEnum.WakeWordLightIndication, false)

    val isMqttApiDeviceChangeEnabled = DeprecatedISetting(DeprecatedSettingsEnum.MqttApiDeviceChangeEnabled, false)
    val isHttpApiDeviceChangeEnabled = DeprecatedISetting(DeprecatedSettingsEnum.HttpApiDeviceChangeEnabled, true)
    val volume = DeprecatedISetting(DeprecatedSettingsEnum.Volume, 0.5F)
    val isHotWordEnabled = DeprecatedISetting(DeprecatedSettingsEnum.HotWordEnabled, true)
    val isAudioOutputEnabled = DeprecatedISetting(DeprecatedSettingsEnum.AudioOutputEnabled, true)
    val isIntentHandlingEnabled = DeprecatedISetting(DeprecatedSettingsEnum.IntentHandlingEnabled, true)

    val wakeSoundVolume = DeprecatedISetting(DeprecatedSettingsEnum.WakeSoundVolume, 0.5F)
    val recordedSoundVolume = DeprecatedISetting(DeprecatedSettingsEnum.RecordedSoundVolume, 0.5F)
    val errorSoundVolume = DeprecatedISetting(DeprecatedSettingsEnum.ErrorSoundVolume, 0.5F)

    val wakeSound = DeprecatedISetting(DeprecatedSettingsEnum.WakeSound, SoundOption.Default.name)
    val recordedSound = DeprecatedISetting(DeprecatedSettingsEnum.RecordedSound, SoundOption.Default.name)
    val errorSound = DeprecatedISetting(DeprecatedSettingsEnum.ErrorSound, SoundOption.Default.name)

    //saves sound as pair, first is fileName as String, second is used and indicates if this custom sound file is used
    val customWakeSounds = DeprecatedISetting(DeprecatedSettingsEnum.CustomWakeSounds, persistentListOf(), StringListSerializer)
    val customRecordedSounds = DeprecatedISetting(DeprecatedSettingsEnum.CustomRecordedSounds, persistentListOf(), StringListSerializer)
    val customErrorSounds = DeprecatedISetting(DeprecatedSettingsEnum.CustomErrorSounds, persistentListOf(), StringListSerializer)

    val isCrashlyticsEnabled = DeprecatedISetting(DeprecatedSettingsEnum.Crashlytics, false)
    val isShowLogEnabled = DeprecatedISetting(DeprecatedSettingsEnum.ShowLog, isDebug())
    val isLogAudioFramesEnabled = DeprecatedISetting(DeprecatedSettingsEnum.LogAudioFrames, false)
    val logLevel = DeprecatedISetting(DeprecatedSettingsEnum.LogLevel, LogLevel.Debug)
    val isLogAutoscroll = DeprecatedISetting(DeprecatedSettingsEnum.LogAutoscroll, true)

    val audioFocusOption = DeprecatedISetting(DeprecatedSettingsEnum.AudioFocusOption, AudioFocusOption.Disabled)
    val isAudioFocusOnNotification = DeprecatedISetting(DeprecatedSettingsEnum.AudioFocusOnNotification, false)
    val isAudioFocusOnSound = DeprecatedISetting(DeprecatedSettingsEnum.AudioFocusOnSound, false)
    val isAudioFocusOnRecord = DeprecatedISetting(DeprecatedSettingsEnum.AudioFocusOnRecord, false)
    val isAudioFocusOnDialog = DeprecatedISetting(DeprecatedSettingsEnum.AudioFocusOnDialog, false)
    val isPauseRecordingOnMedia = DeprecatedISetting(DeprecatedSettingsEnum.AudioRecorderPauseRecordingOnMedia, true)

    val isDialogAutoscroll = DeprecatedISetting(DeprecatedSettingsEnum.DialogAutoScroll, true)
}