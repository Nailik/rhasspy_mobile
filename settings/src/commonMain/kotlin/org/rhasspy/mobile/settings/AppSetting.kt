package org.rhasspy.mobile.settings

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.data.theme.ThemeType
import org.rhasspy.mobile.platformspecific.language.ILanguageUtils
import org.rhasspy.mobile.platformspecific.utils.isDebug
import org.rhasspy.mobile.settings.migrations.SettingsInitializer

/**
 * directly consumed
 */
object AppSetting : KoinComponent {
    val version = ISetting(SettingsEnum.Version, 2)

    val didShowCrashlyticsDialog = ISetting(SettingsEnum.CrashlyticsDialog, false)
    val didShowChangelogDialog = ISetting(SettingsEnum.ChangelogDialog, 0)

    val languageType = ISetting(
        key = SettingsEnum.LanguageOption,
        initial = get<ILanguageUtils>().getDeviceLanguage(),
        serializer = LanguageType.serializer(),
    )
    val themeType = ISetting(
        key = SettingsEnum.ThemeOption,
        initial = ThemeType.System,
        serializer = ThemeType.serializer(),
    )

    val isBackgroundServiceEnabled = ISetting(SettingsEnum.BackgroundEnabled, false)
    val microphoneOverlaySizeOption = ISetting(
        key = SettingsEnum.MicrophoneOverlaySize,
        initial = MicrophoneOverlaySizeOption.Disabled,
        serializer = MicrophoneOverlaySizeOption.serializer(),
    )
    val isMicrophoneOverlayWhileAppEnabled = ISetting(SettingsEnum.MicrophoneOverlayWhileApp, false)
    val microphoneOverlayPositionX = ISetting(SettingsEnum.MicrophoneOverlayPositionX, 0)
    val microphoneOverlayPositionY = ISetting(SettingsEnum.MicrophoneOverlayPositionY, 0)

    val isWakeWordDetectionTurnOnDisplayEnabled = ISetting(SettingsEnum.BackgroundWakeWordDetectionTurnOnDisplay, false)
    val isSoundIndicationEnabled = ISetting(SettingsEnum.SoundIndication, true)
    val soundIndicationOutputOption = ISetting(
        key = SettingsEnum.SoundIndicationOutput,
        initial = AudioOutputOption.Notification,
        serializer = AudioOutputOption.serializer(),
    )
    val isWakeWordLightIndicationEnabled = ISetting(SettingsEnum.WakeWordLightIndication, false)

    val isMqttApiDeviceChangeEnabled = ISetting(SettingsEnum.MqttApiDeviceChangeEnabled, false)
    val isHttpApiDeviceChangeEnabled = ISetting(SettingsEnum.HttpApiDeviceChangeEnabled, true)
    val volume = ISetting(SettingsEnum.Volume, 0.5F)
    val isHotWordEnabled = ISetting(SettingsEnum.HotWordEnabled, true)
    val isAudioOutputEnabled = ISetting(SettingsEnum.AudioOutputEnabled, true)
    val isIntentHandlingEnabled = ISetting(SettingsEnum.IntentHandlingEnabled, true)

    val wakeSoundVolume = ISetting(SettingsEnum.WakeSoundVolume, 0.5F)
    val recordedSoundVolume = ISetting(SettingsEnum.RecordedSoundVolume, 0.5F)
    val errorSoundVolume = ISetting(SettingsEnum.ErrorSoundVolume, 0.5F)

    val wakeSound = ISetting(
        key = SettingsEnum.WakeSound,
        initial = SoundOption.Default.name,
    )
    val recordedSound = ISetting(
        key = SettingsEnum.RecordedSound,
        initial = SoundOption.Default.name,
    )
    val errorSound = ISetting(
        key = SettingsEnum.ErrorSound,
        initial = SoundOption.Default.name,
    )

    //saves sound as pair, first is fileName as String, second is used and indicates if this custom sound file is used
    val customWakeSounds = ISetting(
        key = SettingsEnum.CustomWakeSounds,
        initial = emptyList(),
        serializer = ListSerializer(String.serializer()),
    )
    val customRecordedSounds = ISetting(
        key = SettingsEnum.CustomRecordedSounds,
        initial = emptyList(),
        serializer = ListSerializer(String.serializer()),
    )
    val customErrorSounds = ISetting(
        key = SettingsEnum.CustomErrorSounds,
        initial = emptyList(),
        serializer = ListSerializer(String.serializer()),
    )

    val isCrashlyticsEnabled = ISetting(SettingsEnum.Crashlytics, false)
    val isShowLogEnabled = ISetting(SettingsEnum.ShowLog, isDebug())
    val isLogAudioFramesEnabled = ISetting(SettingsEnum.LogAudioFrames, false)
    val logLevel = ISetting(
        key = SettingsEnum.LogLevel,
        initial = LogLevel.Debug,
        serializer = LogLevel.serializer(),
    )
    val isLogAutoscroll = ISetting(SettingsEnum.LogAutoscroll, true)

    val audioFocusOption = ISetting(
        key = SettingsEnum.AudioFocusOption,
        initial = AudioFocusOption.Disabled,
        serializer = AudioFocusOption.serializer(),
    )
    val isAudioFocusOnNotification = ISetting(SettingsEnum.AudioFocusOnNotification, false)
    val isAudioFocusOnSound = ISetting(SettingsEnum.AudioFocusOnSound, false)
    val isAudioFocusOnRecord = ISetting(SettingsEnum.AudioFocusOnRecord, false)
    val isAudioFocusOnDialog = ISetting(SettingsEnum.AudioFocusOnDialog, false)

    val isDialogAutoscroll = ISetting(SettingsEnum.DialogAutoScroll, true)
}