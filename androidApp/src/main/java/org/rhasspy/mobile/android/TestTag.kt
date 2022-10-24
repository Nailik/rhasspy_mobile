package org.rhasspy.mobile.android

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag

enum class TestTag {
    DialogInformationMicrophonePermission,
    DialogInformationOverlayPermission,
    DialogOk,
    DialogCancel,
    DialogChangelogButton,
    DialogChangelog,
    DialogDataPrivacyButton,
    DialogDataPrivacy,
    DialogLibrary,

    LibrariesContainer,

    AudioPlayingConfigurationScreen,
    AudioPlayingOptions,
    AudioPlayingEndpoint,

    IntentHandlingOptions,

    IntentRecognitionOptions,

    SpeechToTextOptions,

    TextToSpeechOptions,

    WakeWordOptions,
}

fun Modifier.testTag(tag: TestTag) = semantics(
    properties = {
        testTag = tag.name
    }
)