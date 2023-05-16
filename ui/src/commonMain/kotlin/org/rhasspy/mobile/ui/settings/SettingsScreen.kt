package org.rhasspy.mobile.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.ui.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.main.LocalViewModelFactory
import org.rhasspy.mobile.ui.settings.content.*
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
import org.rhasspy.mobile.viewmodel.navigation.Screen.SettingsScreen
import org.rhasspy.mobile.viewmodel.navigation.Screen.SettingsScreen.*
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Navigate
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Navigate.*
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenViewState

@Composable
fun SettingsScreen(screen: SettingsScreen) {

    when (screen) {
        OverviewScreen -> SettingsScreenContent()
        AboutSettings -> AboutScreen()
        AudioFocusSettings -> AudioFocusSettingsContent()
        AudioRecorderSettings -> AudioRecorderSettingsContent()
        AutomaticSilenceDetectionSettings -> SilenceDetectionSettingsContent()
        BackgroundServiceSettings -> BackgroundServiceSettingsContent()
        DeviceSettings -> DeviceSettingsContent()
        is IndicationSettings -> IndicationSettingsContent(screen)
        LanguageSettingsScreen -> LanguageSettingsScreenItemContent()
        LogSettings -> LogSettingsContent()
        MicrophoneOverlaySettings -> MicrophoneOverlaySettingsContent()
        SaveAndRestoreSettings -> SaveAndRestoreSettingsContent()
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent() {

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
                Language(
                    viewState,
                    viewModel::onEvent
                )
                CustomDivider()
            }

            item {
                BackgroundService(
                    viewState.isBackgroundEnabled,
                    viewModel::onEvent
                )
                CustomDivider()
            }

            item {
                MicrophoneOverlay(
                    viewState.microphoneOverlaySizeOption,
                    viewModel::onEvent
                )
                CustomDivider()
            }

            item {
                Indication(
                    isSoundIndicationEnabled = viewState.isSoundIndicationEnabled,
                    isWakeWordLightIndicationEnabled = viewState.isWakeWordLightIndicationEnabled,
                    viewModel::onEvent
                )
                CustomDivider()
            }

            item {
                Device(viewModel::onEvent)
                CustomDivider()
            }

            item {
                AudioFocus(
                    viewState.audioFocusOption,
                    viewModel::onEvent
                )
                CustomDivider()
            }

            item {
                AudioRecorderSettings(
                    audioRecorderChannelType = viewState.audioRecorderChannelType,
                    audioRecorderEncodingType = viewState.audioRecorderEncodingType,
                    audioRecorderSampleRateType = viewState.audioRecorderSampleRateType,
                    viewModel::onEvent
                )
                CustomDivider()
            }

            item {
                AutomaticSilenceDetection(
                    viewState.isAutomaticSilenceDetectionEnabled,
                    viewModel::onEvent
                )
                CustomDivider()
            }

            item {
                Log(
                    viewState.logLevel,
                    viewModel::onEvent
                )
                CustomDivider()
            }

            item {
                SaveAndRestore(viewModel::onEvent)
                CustomDivider()
            }

            item {
                About(viewModel::onEvent)
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
        navigate = LanguageClick,
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
        navigate = BackgroundServiceClick,
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
        navigate = MicrophoneOverlayClick,
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
        navigate = IndicationClick,
        onEvent = onEvent
    )

}

@Composable
private fun Device(onEvent: (event: SettingsScreenUiEvent) -> Unit) {

    SettingsListItem(
        text = MR.strings.device.stable,
        secondaryText = MR.strings.deviceSettingsInformation.stable,
        navigate = DeviceClick,
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
        navigate = AudioFocusClick,
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
        navigate = AudioRecorderSettingsClick,
        onEvent = onEvent
    )

}

@Composable
private fun AutomaticSilenceDetection(
    isAutomaticSilenceDetectionEnabled: Boolean,
    onEvent: (event: SettingsScreenUiEvent) -> Unit
) {

    SettingsListItem(
        text = MR.strings.automaticSilenceDetection.stable,
        secondaryText = isAutomaticSilenceDetectionEnabled.toText(),
        navigate = AutomaticSilenceDetectionClick,
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
        navigate = LogClick,
        onEvent = onEvent
    )

}

@Composable
private fun SaveAndRestore(onEvent: (event: SettingsScreenUiEvent) -> Unit) {

    SettingsListItem(
        text = MR.strings.saveAndRestoreSettings.stable,
        secondaryText = MR.strings.backup.stable,
        navigate = SaveAndRestoreClick,
        onEvent = onEvent
    )

}

@Composable
private fun About(onEvent: (event: SettingsScreenUiEvent) -> Unit) {

    SettingsListItem(
        text = MR.strings.aboutTitle.stable,
        secondaryText = "${translate(MR.strings.version.stable)} ${BuildKonfig.versionName}",
        navigate = Navigate.AboutClick,
        onEvent = onEvent
    )

}


@Composable
private fun SettingsListItem(
    text: StableStringResource,
    secondaryText: StableStringResource? = null,
    navigate: Navigate,
    onEvent: (navigate: Navigate) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(navigate) }
            .testTag(navigate.toString()),
        text = { org.rhasspy.mobile.ui.content.elements.Text(text) },
        secondaryText = { secondaryText?.also { org.rhasspy.mobile.ui.content.elements.Text(secondaryText) } }
    )
}

@Composable
private fun SettingsListItem(
    text: StableStringResource,
    secondaryText: String,
    navigate: Navigate,
    onEvent: (navigate: Navigate) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(navigate) }
            .testTag(navigate.toString()),
        text = { org.rhasspy.mobile.ui.content.elements.Text(text) },
        secondaryText = { Text(text = secondaryText) }
    )
}