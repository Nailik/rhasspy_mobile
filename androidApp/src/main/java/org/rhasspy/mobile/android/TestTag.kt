package org.rhasspy.mobile.android

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import org.rhasspy.mobile.data.DataEnum

/**
 * test tags to be used in compose
 */
enum class TestTag {
    DialogCrashlytics,
    DialogInformationMicrophonePermission,
    DialogInformationOverlayPermission,
    DialogOk,
    DialogCancel,
    DialogUnsavedChanges,
    DialogChangelogButton,
    DialogChangelog,
    DialogDataPrivacyButton,
    DialogDataPrivacy,
    DialogLibrary,
    AppBarBackButton,
    AppBarTitle,
    BottomAppBarDiscard,
    BottomAppBarSave,
    BottomAppBarTest,
    DialogManagementOptions,
    LibrariesContainer,
    MicrophoneOverlaySizeOptions,
    MicrophoneFab,

    AudioPlayingOptions,
    AudioOutputOptions,
    Endpoint,
    TextToSpeechText,
    AccessToken,
    SendEvents,
    SendIntents,
    AudioRecordingUdpOutput,
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

    ServerSwitch,
    MqttSwitch,
    Host,
    Port,
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
    Background,
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

    Volume,
    HotWord,
    AudioOutput,
    IntentHandling,
    AutomaticSilenceDetectionSettingsConfiguration,
    AutomaticSilenceDetectionSettingsTime,
    AutomaticSilenceDetectionSettingsAudioLevelTest,
    AutomaticSilenceDetectionSettingsTest,
    CrashlyticsEnabled,
    ShowLogEnabled,
    AudioFramesEnabled
}

fun Modifier.combinedTestTag(dataEnum: DataEnum<*>, tag: TestTag) = semantics(
    properties = {
        testTag = "${dataEnum.name}${tag.name}"
    }
)


fun Modifier.combinedTestTag(name: String, tag: TestTag) = semantics(
    properties = {
        testTag = "$name${tag.name}"
    }
)

fun Modifier.testTag(enum: Enum<*>) = semantics(
    properties = {
        testTag = enum.name
    }
)

fun Modifier.testTag(dataEnum: DataEnum<*>) = semantics(
    properties = {
        testTag = dataEnum.name
    }
)

fun Modifier.testTag(name: String) = semantics(
    properties = {
        testTag = name
    }
)