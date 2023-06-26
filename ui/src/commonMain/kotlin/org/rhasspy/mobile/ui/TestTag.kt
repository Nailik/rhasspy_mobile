package org.rhasspy.mobile.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

/**
 * test tags to be used in compose
 */
enum class TestTag {
    List,
    AppBarBackButton,
    AppBarTitle,
    BottomAppBarDiscard,
    BottomAppBarSave,
    LibrariesContainer,
    MicrophoneOverlaySizeOptions,
    MicrophoneFab,
    Indication,
    Overlay,

    AudioPlayingOptions,
    AudioOutputOptions,
    Endpoint,
    TextToSpeechText,
    AccessToken,
    SendEvents,
    SendIntents,
    AudioRecordingUdpHost,
    AudioRecordingUdpPort,
    IntentNameText,
    IntentText,

    ConfigurationScreenItemContent,
    ConfigurationSiteId,
    CustomEndpointSwitch,
    MqttSilenceDetectionSwitch,

    IntentHandlingOptions,

    IntentRecognitionOptions,

    SpeechToTextOptions,

    TextToSpeechOptions,

    WakeWordOptions,

    TextAsrTimeout,
    IntentRecognitionTimeout,
    RecordingTimeout,

    ServerSwitch,
    MqttSwitch,
    Host,
    Port,
    Timeout,
    UserName,
    Password,
    SSLSwitch,
    CertificateButton,
    ConnectionTimeout,
    KeepAliveInterval,
    RetryInterval,

    PorcupineWakeWordSettings,
    PorcupineAccessToken,
    PorcupineOpenConsole,
    PorcupineKeyword,
    PorcupineLanguage,
    PorcupineKeywordScreen,
    PorcupineLanguageScreen,
    PorcupineKeywordCustomScreen,
    PorcupineKeywordDefaultScreen,

    WebServerSSLWiki,
    MQTTSSLWiki,

    TabDefault,
    TabCustom,
    Sensitivity,
    Download,
    SelectFile,
    PlayPause,
    Delete,
    Undo,
    EnabledSwitch,
    BatteryOptimization,
    VisibleWhileAppIsOpened,
    IndicationSoundScreen,

    WakeWordDetectionTurnOnDisplay,
    WakeWordLightIndicationEnabled,
    SoundIndicationEnabled,

    Disabled,
    Default,
    Warning,

    AudioFocusOption,
    AudioFocusSettingsConfiguration,
    AudioFocusOnNotification,
    AudioFocusOnSound,
    AudioFocusOnRecord,
    AudioFocusOnDialog,

    AudioRecorderEncodingType,
    AudioRecorderChannelType,
    AudioRecorderSampleRateType,

    Volume,
    HotWord,
    AudioOutput,
    IntentHandling,
    AutomaticSilenceDetectionSettingsConfiguration,
    AutomaticSilenceDetectionSettingsMinimumTime,
    AutomaticSilenceDetectionSettingsTime,
    AutomaticSilenceDetectionSettingsAudioLevelTest,
    AutomaticSilenceDetectionSettingsTest,
    CrashlyticsEnabled,
    ShowLogEnabled,
    AudioFramesEnabled,


    OpenConfigScreen,


    DialogManagementOptions,

    DialogOk,
    DialogCancel,
    DialogUnsavedChanges,
    DialogChangelogButton,
    DialogChangelog,
    DialogDataPrivacyButton,
    DialogDataPrivacy,
    DialogLibrary,
    DialogServiceState,
    DialogCrashlytics,
    DialogMicrophonePermissionInfo,
    DialogOverlayPermissionInfo,
    DialogSaveSettings,
    DialogRestoreSettings
}

fun Modifier.combinedTestTag(IOption: IOption<*>, tag: TestTag) = semantics(
    properties = {
        testTag = "${IOption.name}${tag.name}"
    }
)


fun Modifier.combinedTestTag(name: String, tag: TestTag) = semantics(
    properties = {
        testTag = "$name${tag.name}"
    }
)

fun Modifier.combinedTestTag(name: TestTag, tag: TestTag) = semantics(
    properties = {
        testTag = "${name.name}${tag.name}"
    }
)


fun Modifier.testTag(enum: TestTag) = semantics(
    properties = {
        testTag = enum.name
    }
)

fun Modifier.testTag(option: IOption<*>) = semantics(
    properties = {
        testTag = option.name
    }
)

fun Modifier.testTag(name: String) = semantics(
    properties = {
        testTag = name
    }
)

fun Modifier.testTag(name: StableStringResource) = semantics(
    properties = {
        testTag = name.toString()
    }
)

fun Modifier.testTag(name: NavigationDestination) = semantics(
    properties = {
        testTag = name.toString()
    }
)