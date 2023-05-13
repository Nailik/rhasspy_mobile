package org.rhasspy.mobile.android.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.android.content.elements.*
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.android.settings.content.*
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.toText
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenViewState

@Preview
@Composable
fun SettingsScreen() {
    val viewModel: SettingsScreenViewModel = LocalViewModelFactory.current.getViewModel()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { org.rhasspy.mobile.ui.content.elements.Text(MR.strings.settings.stable) }
            )
        },
    ) { paddingValues ->

        val viewState by viewModel.viewState.collectAsState()

        LazyColumn(
            Modifier
                .testTag(TestTag.List)
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            item {
                Language(viewState)
                CustomDivider()
            }

            item {
                BackgroundService(viewState.isBackgroundEnabled)
                CustomDivider()
            }

            item {
                MicrophoneOverlay(viewState.microphoneOverlaySizeOption)
                CustomDivider()
            }

            item {
                Indication(
                    isSoundIndicationEnabled = viewState.isSoundIndicationEnabled,
                    isWakeWordLightIndicationEnabled = viewState.isWakeWordLightIndicationEnabled
                )
                CustomDivider()
            }

            item {
                Device()
                CustomDivider()
            }

            item {
                AudioFocus(viewState.audioFocusOption)
                CustomDivider()
            }

            item {
                AudioRecorderSettings(
                    audioRecorderChannelType = viewState.audioRecorderChannelType,
                    audioRecorderEncodingType = viewState.audioRecorderEncodingType,
                    audioRecorderSampleRateType = viewState.audioRecorderSampleRateType
                )
                CustomDivider()
            }

            item {
                AutomaticSilenceDetection(viewState.isAutomaticSilenceDetectionEnabled)
                CustomDivider()
            }

            item {
                Log(viewState.logLevel)
                CustomDivider()
            }

            item {
                SaveAndRestore()
                CustomDivider()
            }

            item {
                About()
                CustomDivider()
            }

        }
    }

}

/**
 * configuration screens with list items that open bottom sheet
 */
fun NavGraphBuilder.addSettingsScreen() {

    composable(SettingsScreenType.LanguageSettings.route) {
        LanguageSettingsScreenItemContent()
    }

    composable(SettingsScreenType.BackgroundServiceSettings.route) {
        BackgroundServiceSettingsContent()
    }

    composable(SettingsScreenType.MicrophoneOverlaySettings.route) {
        MicrophoneOverlaySettingsContent()
    }

    composable(SettingsScreenType.IndicationSettings.route) {
        WakeWordIndicationSettingsContent()
    }

    composable(SettingsScreenType.DeviceSettings.route) {
        DeviceSettingsContent()
    }

    composable(SettingsScreenType.AudioFocusSettings.route) {
        AudioFocusSettingsContent()
    }

    composable(SettingsScreenType.AudioRecorderSettings.route) {
        AudioRecorderSettingsContent()
    }

    composable(SettingsScreenType.AutomaticSilenceDetectionSettings.route) {
        AutomaticSilenceDetectionSettingsContent()
    }

    composable(SettingsScreenType.LogSettings.route) {
        LogSettingsContent()
    }

    composable(SettingsScreenType.SaveAndRestoreSettings.route) {
        SaveAndRestoreSettingsContent()
    }

    composable(SettingsScreenType.AboutSettings.route) {
        AboutScreen()
    }

}

@Composable
private fun Language(viewState: SettingsScreenViewState) {

    SettingsListItem(
        text = MR.strings.language.stable,
        secondaryText = viewState.currentLanguage.text,
        screen = SettingsScreenType.LanguageSettings
    )

}

@Composable
private fun BackgroundService(isBackgroundEnabled: Boolean) {

    SettingsListItem(
        text = MR.strings.background.stable,
        secondaryText = isBackgroundEnabled.toText(),
        screen = SettingsScreenType.BackgroundServiceSettings
    )

}

@Composable
private fun MicrophoneOverlay(microphoneOverlaySizeOption: MicrophoneOverlaySizeOption) {

    SettingsListItem(
        text = MR.strings.microphoneOverlay.stable,
        secondaryText = microphoneOverlaySizeOption.name,
        screen = SettingsScreenType.MicrophoneOverlaySettings
    )

}

@Composable
private fun Indication(
    isSoundIndicationEnabled: Boolean,
    isWakeWordLightIndicationEnabled: Boolean
) {

    var stateText = if (isSoundIndicationEnabled) translate(MR.strings.sound.stable) else ""
    if (isWakeWordLightIndicationEnabled) {
        if (stateText.isNotEmpty()) {
            stateText += " ${translate(MR.strings._and.stable)} "
        }
        stateText += translate(MR.strings.light.stable)
    }
    if (stateText.isEmpty()) {
        stateText = translate(MR.strings.disabled.stable)
    }

    SettingsListItem(
        text = MR.strings.indication.stable,
        secondaryText = stateText,
        screen = SettingsScreenType.IndicationSettings
    )

}

@Composable
private fun Device() {

    SettingsListItem(
        text = MR.strings.device.stable,
        secondaryText = MR.strings.deviceSettingsInformation.stable,
        screen = SettingsScreenType.DeviceSettings
    )

}

@Composable
private fun AudioFocus(audioFocusOption: AudioFocusOption) {

    SettingsListItem(
        text = MR.strings.audioFocus.stable,
        secondaryText = audioFocusOption.text,
        screen = SettingsScreenType.AudioFocusSettings
    )

}


@Composable
private fun AudioRecorderSettings(
    audioRecorderChannelType: AudioRecorderChannelType,
    audioRecorderEncodingType: AudioRecorderEncodingType,
    audioRecorderSampleRateType: AudioRecorderSampleRateType
) {

    SettingsListItem(
        text = MR.strings.audioRecorder.stable,
        secondaryText = "${translate(audioRecorderChannelType.text)} | ${translate(audioRecorderEncodingType.text)} | ${translate(audioRecorderSampleRateType.text)}",
        screen = SettingsScreenType.AudioRecorderSettings
    )

}

@Composable
private fun AutomaticSilenceDetection(isAutomaticSilenceDetectionEnabled: Boolean) {

    SettingsListItem(
        text = MR.strings.automaticSilenceDetection.stable,
        secondaryText = isAutomaticSilenceDetectionEnabled.toText(),
        screen = SettingsScreenType.AutomaticSilenceDetectionSettings
    )

}

@Composable
private fun Log(logLevel: LogLevel) {

    SettingsListItem(
        text = MR.strings.logSettings.stable,
        secondaryText = logLevel.text,
        screen = SettingsScreenType.LogSettings
    )

}

@Composable
private fun SaveAndRestore() {

    SettingsListItem(
        text = MR.strings.saveAndRestoreSettings.stable,
        secondaryText = MR.strings.backup.stable,
        screen = SettingsScreenType.SaveAndRestoreSettings
    )

}

@Composable
private fun About() {

    SettingsListItem(
        text = MR.strings.aboutTitle.stable,
        secondaryText = "${translate(MR.strings.version.stable)} ${BuildKonfig.versionName}",
        screen = SettingsScreenType.AboutSettings
    )

}


@Composable
private fun SettingsListItem(
    text: StableStringResource,
    secondaryText: StableStringResource? = null,
    screen: SettingsScreenType
) {
    val navController = LocalMainNavController.current

    ListElement(
        modifier = Modifier
            .clickable {
                navController.navigate(screen.route)
            }
            .testTag(screen),
        text = { org.rhasspy.mobile.ui.content.elements.Text(text) },
        secondaryText = { secondaryText?.also { org.rhasspy.mobile.ui.content.elements.Text(secondaryText) } }
    )
}

@Composable
private fun SettingsListItem(
    text: StableStringResource,
    secondaryText: String,
    @Suppress("SameParameterValue") screen: SettingsScreenType
) {
    val navController = LocalMainNavController.current

    ListElement(
        modifier = Modifier
            .clickable {
                navController.navigate(screen.route)
            }
            .testTag(screen),
        text = { org.rhasspy.mobile.ui.content.elements.Text(text) },
        secondaryText = { Text(text = secondaryText) }
    )
}