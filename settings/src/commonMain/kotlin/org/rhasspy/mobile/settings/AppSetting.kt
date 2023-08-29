package org.rhasspy.mobile.settings

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
import org.rhasspy.mobile.settings.types.*

/**
 * directly consumed
 */
object AppSetting : KoinComponent {

    val didShowCrashlyticsDialog = BooleanSetting(SettingsEnum.CrashlyticsDialog, false)
    val didShowChangelogDialog = IntSetting(SettingsEnum.ChangelogDialog, 0)

    val languageType = IOptionSetting(SettingsEnum.LanguageOption, get<ILanguageUtils>().getDeviceLanguage())
    val themeType = IOptionSetting(SettingsEnum.ThemeOption, ThemeType.System)

    val isBackgroundServiceEnabled = BooleanSetting(SettingsEnum.BackgroundEnabled, false)
    val microphoneOverlaySizeOption = IOptionSetting(SettingsEnum.MicrophoneOverlaySize, MicrophoneOverlaySizeOption.Disabled)
    val isMicrophoneOverlayWhileAppEnabled = BooleanSetting(SettingsEnum.MicrophoneOverlayWhileApp, false)
    val microphoneOverlayPositionX = IntSetting(SettingsEnum.MicrophoneOverlayPositionX, 0)
    val microphoneOverlayPositionY = IntSetting(SettingsEnum.MicrophoneOverlayPositionY, 0)

    val isWakeWordDetectionTurnOnDisplayEnabled = BooleanSetting(SettingsEnum.BackgroundWakeWordDetectionTurnOnDisplay, false)
    val isSoundIndicationEnabled = BooleanSetting(SettingsEnum.SoundIndication, true)
    val soundIndicationOutputOption = IOptionSetting(SettingsEnum.SoundIndicationOutput, AudioOutputOption.Notification)
    val isWakeWordLightIndicationEnabled = BooleanSetting(SettingsEnum.WakeWordLightIndication, false)

    val isMqttApiDeviceChangeEnabled = BooleanSetting(SettingsEnum.MqttApiDeviceChangeEnabled, false)
    val isHttpApiDeviceChangeEnabled = BooleanSetting(SettingsEnum.HttpApiDeviceChangeEnabled, true)
    val volume = FloatSetting(SettingsEnum.Volume, 0.5F)
    val isHotWordEnabled = BooleanSetting(SettingsEnum.HotWordEnabled, true)
    val isAudioOutputEnabled = BooleanSetting(SettingsEnum.AudioOutputEnabled, true)
    val isIntentHandlingEnabled = BooleanSetting(SettingsEnum.IntentHandlingEnabled, true)

    val wakeSoundVolume = FloatSetting(SettingsEnum.WakeSoundVolume, 0.5F)
    val recordedSoundVolume = FloatSetting(SettingsEnum.RecordedSoundVolume, 0.5F)
    val errorSoundVolume = FloatSetting(SettingsEnum.ErrorSoundVolume, 0.5F)

    val wakeSound = StringSetting(SettingsEnum.WakeSound, SoundOption.Default.name)
    val recordedSound = StringSetting(SettingsEnum.RecordedSound, SoundOption.Default.name)
    val errorSound = StringSetting(SettingsEnum.ErrorSound, SoundOption.Default.name)

    //saves sound as pair, first is fileName as String, second is used and indicates if this custom sound file is used
    val customWakeSounds = StringListSetting(SettingsEnum.CustomWakeSounds, persistentListOf())
    val customRecordedSounds = StringListSetting(SettingsEnum.CustomRecordedSounds, persistentListOf())
    val customErrorSounds = StringListSetting(SettingsEnum.CustomErrorSounds, persistentListOf())

    val isCrashlyticsEnabled = BooleanSetting(SettingsEnum.Crashlytics, false)
    val isShowLogEnabled = BooleanSetting(SettingsEnum.ShowLog, isDebug())
    val isLogAudioFramesEnabled = BooleanSetting(SettingsEnum.LogAudioFrames, false)
    val logLevel = IOptionSetting(SettingsEnum.LogLevel, LogLevel.Debug)
    val isLogAutoscroll = BooleanSetting(SettingsEnum.LogAutoscroll, true)

    val audioFocusOption = IOptionSetting(SettingsEnum.AudioFocusOption, AudioFocusOption.Disabled)
    val isAudioFocusOnNotification = BooleanSetting(SettingsEnum.AudioFocusOnNotification, false)
    val isAudioFocusOnSound = BooleanSetting(SettingsEnum.AudioFocusOnSound, false)
    val isAudioFocusOnRecord = BooleanSetting(SettingsEnum.AudioFocusOnRecord, false)
    val isAudioFocusOnDialog = BooleanSetting(SettingsEnum.AudioFocusOnDialog, false)
    val isPauseRecordingOnMedia = BooleanSetting(SettingsEnum.AudioRecorderPauseRecordingOnMedia, true)

    val isDialogAutoscroll = BooleanSetting(SettingsEnum.DialogAutoScroll, true)
}