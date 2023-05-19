package org.rhasspy.mobile.android.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.BuildKonfig
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
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.toText
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.SettingsScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.*
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenViewState

@Composable
fun SettingsScreen() {

    val viewModel: SettingsScreenViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(viewModel) {
        val screen by viewModel.screen.collectAsState()

        when (screen) {
            null -> {
                val viewState by viewModel.viewState.collectAsState()

                SettingsScreenContent(
                    viewState = viewState,
                    onEvent = viewModel::onEvent
                )
            }

            AboutSettings -> AboutScreen()
            AudioFocusSettings -> AudioFocusSettingsContent()
            AudioRecorderSettings -> AudioRecorderSettingsContent()
            SilenceDetectionSettings -> SilenceDetectionSettingsContent()
            BackgroundServiceSettings -> BackgroundServiceSettingsContent()
            DeviceSettings -> DeviceSettingsContent()
            IndicationSettings -> IndicationSettingsContent()
            LanguageSettingsScreen -> LanguageSettingsScreenItemContent()
            LogSettings -> LogSettingsContent()
            MicrophoneOverlaySettings -> MicrophoneOverlaySettingsContent()
            SaveAndRestoreSettings -> SaveAndRestoreSettingsContent()
        }

    }

}


@Composable
fun SettingsScreenContent(
    viewState: SettingsScreenViewState,
    onEvent: (event: SettingsScreenUiEvent) -> Unit
) {

    Scaffold(
        modifier = Modifier
            .testTag(SettingsScreen)
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(MR.strings.settings.stable) }
            )
        },
    ) { paddingValues ->

        LazyColumn(
            Modifier
                .testTag(TestTag.List)
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            item {
                Language(
                    viewState,
                    onEvent
                )
                CustomDivider()
            }

            item {
                BackgroundService(
                    viewState.isBackgroundEnabled,
                    onEvent
                )
                CustomDivider()
            }

            item {
                MicrophoneOverlay(
                    viewState.microphoneOverlaySizeOption,
                    onEvent
                )
                CustomDivider()
            }

            item {
                Indication(
                    isSoundIndicationEnabled = viewState.isSoundIndicationEnabled,
                    isWakeWordLightIndicationEnabled = viewState.isWakeWordLightIndicationEnabled,
                    onEvent
                )
                CustomDivider()
            }

            item {
                Device(onEvent)
                CustomDivider()
            }

            item {
                AudioFocus(
                    viewState.audioFocusOption,
                    onEvent
                )
                CustomDivider()
            }

            item {
                AudioRecorderSettings(
                    audioRecorderChannelType = viewState.audioRecorderChannelType,
                    audioRecorderEncodingType = viewState.audioRecorderEncodingType,
                    audioRecorderSampleRateType = viewState.audioRecorderSampleRateType,
                    onEvent
                )
                CustomDivider()
            }

            item {
                SilenceDetection(
                    viewState.isAutomaticSilenceDetectionEnabled,
                    onEvent
                )
                CustomDivider()
            }

            item {
                Log(
                    viewState.logLevel,
                    onEvent
                )
                CustomDivider()
            }

            item {
                SaveAndRestore(onEvent)
                CustomDivider()
            }

            item {
                About(onEvent)
                CustomDivider()
            }

        }
    }

}


@Composable
private fun Language(
    viewState: SettingsScreenViewState,
    onEvent: (event: SettingsScreenUiEvent) -> Unit
) {

    SettingsListItem(
        text = MR.strings.language.stable,
        secondaryText = viewState.currentLanguage.text,
        destination = LanguageSettingsScreen,
        onEvent = onEvent
    )

}

@Composable
private fun BackgroundService(
    isBackgroundEnabled: Boolean,
    onEvent: (event: SettingsScreenUiEvent) -> Unit
) {

    SettingsListItem(
        text = MR.strings.background.stable,
        secondaryText = isBackgroundEnabled.toText(),
        destination = BackgroundServiceSettings,
        onEvent = onEvent
    )

}

@Composable
private fun MicrophoneOverlay(
    microphoneOverlaySizeOption: MicrophoneOverlaySizeOption,
    onEvent: (event: SettingsScreenUiEvent) -> Unit
) {

    SettingsListItem(
        text = MR.strings.microphoneOverlay.stable,
        secondaryText = microphoneOverlaySizeOption.name,
        destination = MicrophoneOverlaySettings,
        onEvent = onEvent
    )

}

@Composable
private fun Indication(
    isSoundIndicationEnabled: Boolean,
    isWakeWordLightIndicationEnabled: Boolean,
    onEvent: (event: SettingsScreenUiEvent) -> Unit
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
        destination = IndicationSettings,
        onEvent = onEvent
    )

}

@Composable
private fun Device(onEvent: (event: SettingsScreenUiEvent) -> Unit) {

    SettingsListItem(
        text = MR.strings.device.stable,
        secondaryText = MR.strings.deviceSettingsInformation.stable,
        destination = DeviceSettings,
        onEvent = onEvent
    )

}

@Composable
private fun AudioFocus(
    audioFocusOption: AudioFocusOption,
    onEvent: (event: SettingsScreenUiEvent) -> Unit
) {

    SettingsListItem(
        text = MR.strings.audioFocus.stable,
        secondaryText = audioFocusOption.text,
        destination = AudioFocusSettings,
        onEvent = onEvent
    )

}


@Composable
private fun AudioRecorderSettings(
    audioRecorderChannelType: AudioRecorderChannelType,
    audioRecorderEncodingType: AudioRecorderEncodingType,
    audioRecorderSampleRateType: AudioRecorderSampleRateType,
    onEvent: (event: SettingsScreenUiEvent) -> Unit
) {

    SettingsListItem(
        text = MR.strings.audioRecorder.stable,
        secondaryText = "${translate(audioRecorderChannelType.text)} | ${translate(audioRecorderEncodingType.text)} | ${translate(audioRecorderSampleRateType.text)}",
        destination = AudioRecorderSettings,
        onEvent = onEvent
    )

}

@Composable
private fun SilenceDetection(
    isSilenceDetectionEnabled: Boolean,
    onEvent: (event: SettingsScreenUiEvent) -> Unit
) {

    SettingsListItem(
        text = MR.strings.automaticSilenceDetection.stable,
        secondaryText = isSilenceDetectionEnabled.toText(),
        destination = SilenceDetectionSettings,
        onEvent = onEvent
    )

}

@Composable
private fun Log(
    logLevel: LogLevel,
    onEvent: (event: SettingsScreenUiEvent) -> Unit
) {

    SettingsListItem(
        text = MR.strings.logSettings.stable,
        secondaryText = logLevel.text,
        destination = LogSettings,
        onEvent = onEvent
    )

}

@Composable
private fun SaveAndRestore(onEvent: (event: SettingsScreenUiEvent) -> Unit) {

    SettingsListItem(
        text = MR.strings.saveAndRestoreSettings.stable,
        secondaryText = MR.strings.backup.stable,
        destination = SaveAndRestoreSettings,
        onEvent = onEvent
    )

}

@Composable
private fun About(onEvent: (event: SettingsScreenUiEvent) -> Unit) {

    SettingsListItem(
        text = MR.strings.aboutTitle.stable,
        secondaryText = "${translate(MR.strings.version.stable)} ${BuildKonfig.versionName}",
        destination = AboutSettings,
        onEvent = onEvent
    )

}


@Composable
private fun SettingsListItem(
    text: StableStringResource,
    secondaryText: StableStringResource? = null,
    destination: SettingsScreenDestination,
    onEvent: (navigate: Navigate) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(destination)) }
            .testTag(destination),
        text = { Text(text) },
        secondaryText = { secondaryText?.also { Text(secondaryText) } }
    )
}

@Composable
private fun SettingsListItem(
    text: StableStringResource,
    secondaryText: String,
    destination: SettingsScreenDestination,
    onEvent: (navigate: Navigate) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(destination)) }
            .testTag(destination),
        text = { Text(text) },
        secondaryText = { Text(text = secondaryText) }
    )
}