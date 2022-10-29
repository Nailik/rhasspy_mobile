package org.rhasspy.mobile.android

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import org.rhasspy.mobile.data.DataEnum

enum class TestTag {
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

    AudioPlayingOptions,
    Endpoint,
    AccessToken,
    SendEvents,
    SendIntents,
    AudioRecordingUdpOutput,
    AudioRecordingUdpHost,
    AudioRecordingUdpPort,

    ConfigurationScreenItemContent,
    ConfigurationSiteId,
    CustomEndpointSwitch,

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


    TabDefault,
    TabCustom
}

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