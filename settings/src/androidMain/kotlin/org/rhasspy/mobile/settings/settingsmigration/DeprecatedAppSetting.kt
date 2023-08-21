package org.rhasspy.mobile.settings.settingsmigration

import kotlinx.collections.immutable.persistentListOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.data.theme.ThemeType
import org.rhasspy.mobile.platformspecific.language.ILanguageUtils
import org.rhasspy.mobile.platformspecific.utils.isDebug

/**
 * directly consumed
 */
object DeprecatedAppSetting : KoinComponent {

    val didShowCrashlyticsDialog = DeprecatedISetting(SettingsEnum.CrashlyticsDialog, false)
    val didShowChangelogDialog = DeprecatedISetting(SettingsEnum.ChangelogDialog, 0)

    val languageType = DeprecatedISetting(SettingsEnum.LanguageOption, get<ILanguageUtils>().getDeviceLanguage())
    val themeType = DeprecatedISetting(SettingsEnum.ThemeOption, ThemeType.System)

    val isAutomaticSilenceDetectionEnabled = DeprecatedISetting(SettingsEnum.AutomaticSilenceDetection, false)
    val automaticSilenceDetectionAudioLevel = DeprecatedISetting(SettingsEnum.AutomaticSilenceDetectionAudioLevel, 40f)
    val automaticSilenceDetectionTime = DeprecatedISetting<Long?>(SettingsEnum.AutomaticSilenceDetectionTime, 2000)
    val automaticSilenceDetectionMinimumTime = DeprecatedISetting<Long?>(SettingsEnum.AutomaticSilenceDetectionMinimumTime, 2000)

    val isBackgroundServiceEnabled = DeprecatedISetting(SettingsEnum.BackgroundEnabled, false)
    val microphoneOverlaySizeOption = DeprecatedISetting(SettingsEnum.MicrophoneOverlaySize, MicrophoneOverlaySizeOption.Disabled)
    val isMicrophoneOverlayWhileAppEnabled = DeprecatedISetting(SettingsEnum.MicrophoneOverlayWhileApp, false)
    val microphoneOverlayPositionX = DeprecatedISetting(SettingsEnum.MicrophoneOverlayPositionX, 0)
    val microphoneOverlayPositionY = DeprecatedISetting(SettingsEnum.MicrophoneOverlayPositionY, 0)

    val isWakeWordDetectionTurnOnDisplayEnabled = DeprecatedISetting(SettingsEnum.BackgroundWakeWordDetectionTurnOnDisplay, false)
    val isSoundIndicationEnabled = DeprecatedISetting(SettingsEnum.SoundIndication, true)
    val soundIndicationOutputOption = DeprecatedISetting(SettingsEnum.SoundIndicationOutput, AudioOutputOption.Notification)
    val isWakeWordLightIndicationEnabled = DeprecatedISetting(SettingsEnum.WakeWordLightIndication, false)

    val isMqttApiDeviceChangeEnabled = DeprecatedISetting(SettingsEnum.MqttApiDeviceChangeEnabled, false)
    val isHttpApiDeviceChangeEnabled = DeprecatedISetting(SettingsEnum.HttpApiDeviceChangeEnabled, true)
    val volume = DeprecatedISetting(SettingsEnum.Volume, 0.5F)
    val isHotWordEnabled = DeprecatedISetting(SettingsEnum.HotWordEnabled, true)
    val isAudioOutputEnabled = DeprecatedISetting(SettingsEnum.AudioOutputEnabled, true)
    val isIntentHandlingEnabled = DeprecatedISetting(SettingsEnum.IntentHandlingEnabled, true)

    val wakeSoundVolume = DeprecatedISetting(SettingsEnum.WakeSoundVolume, 0.5F)
    val recordedSoundVolume = DeprecatedISetting(SettingsEnum.RecordedSoundVolume, 0.5F)
    val errorSoundVolume = DeprecatedISetting(SettingsEnum.ErrorSoundVolume, 0.5F)

    val wakeSound = DeprecatedISetting(SettingsEnum.WakeSound, SoundOption.Default.name)
    val recordedSound = DeprecatedISetting(SettingsEnum.RecordedSound, SoundOption.Default.name)
    val errorSound = DeprecatedISetting(SettingsEnum.ErrorSound, SoundOption.Default.name)

    //saves sound as pair, first is fileName as String, second is used and indicates if this custom sound file is used
    val customWakeSounds = DeprecatedISetting(SettingsEnum.CustomWakeSounds, persistentListOf(), StringListSerializer)
    val customRecordedSounds = DeprecatedISetting(SettingsEnum.CustomRecordedSounds, persistentListOf(), StringListSerializer)
    val customErrorSounds = DeprecatedISetting(SettingsEnum.CustomErrorSounds, persistentListOf(), StringListSerializer)

    val isCrashlyticsEnabled = DeprecatedISetting(SettingsEnum.Crashlytics, false)
    val isShowLogEnabled = DeprecatedISetting(SettingsEnum.ShowLog, isDebug())
    val isLogAudioFramesEnabled = DeprecatedISetting(SettingsEnum.LogAudioFrames, false)
    val logLevel = DeprecatedISetting(SettingsEnum.LogLevel, LogLevel.Debug)
    val isLogAutoscroll = DeprecatedISetting(SettingsEnum.LogAutoscroll, true)

    val audioFocusOption = DeprecatedISetting(SettingsEnum.AudioFocusOption, AudioFocusOption.Disabled)
    val isAudioFocusOnNotification = DeprecatedISetting(SettingsEnum.AudioFocusOnNotification, false)
    val isAudioFocusOnSound = DeprecatedISetting(SettingsEnum.AudioFocusOnSound, false)
    val isAudioFocusOnRecord = DeprecatedISetting(SettingsEnum.AudioFocusOnRecord, false)
    val isAudioFocusOnDialog = DeprecatedISetting(SettingsEnum.AudioFocusOnDialog, false)
    val isPauseRecordingOnMedia = DeprecatedISetting(SettingsEnum.AudioRecorderPauseRecordingOnMedia, true)

    val isDialogAutoscroll = DeprecatedISetting(SettingsEnum.DialogAutoScroll, true)
}