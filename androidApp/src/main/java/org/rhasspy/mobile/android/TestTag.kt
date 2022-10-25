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
    AudioPlayingHttpEndpoint,
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
}

fun Modifier.testTag(tag: Enum<*>) = semantics(
    properties = {
        testTag = tag.name
    }
)

fun Modifier.testTag(tag: DataEnum<*>) = semantics(
    properties = {
        testTag = tag.name
    }
)