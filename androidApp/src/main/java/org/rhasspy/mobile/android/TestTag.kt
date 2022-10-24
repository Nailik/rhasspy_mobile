package org.rhasspy.mobile.android

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreens

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
    AppBarBackButton,

    LibrariesContainer,

    AudioPlayingConfigurationScreen,
    AudioPlayingOptions,
    AudioPlayingEndpoint,

    ConfigurationScreenItemContent,
    ConfigurationSiteId,


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