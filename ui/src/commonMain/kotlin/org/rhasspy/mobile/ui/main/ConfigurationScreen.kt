package org.rhasspy.mobile.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ConnectionState.ErrorState
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.item.EventStateIconTinted
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.utils.ListType.ConfigurationScreenList
import org.rhasspy.mobile.ui.utils.rememberForeverScrollState
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination.ConfigurationScreen
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.OpenWikiLink
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change.SiteIdChange
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.*

/**
 * configuration screens with list items that open bottom sheet
 */
@Composable
fun ConfigurationScreen(viewModel: ConfigurationScreenViewModel) {

    ScreenContent(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        ConfigurationScreenContent(
            onEvent = viewModel::onEvent,
            viewState = viewState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigurationScreenContent(
    onEvent: (ConfigurationScreenUiEvent) -> Unit,
    viewState: ConfigurationScreenViewState
) {
    val scrollState = rememberForeverScrollState(ConfigurationScreenList)

    Scaffold(
        modifier = Modifier
            .testTag(ConfigurationScreen)
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(MR.strings.configuration.stable) },
                actions = {
                    IconButton(
                        onClick = { onEvent(OpenWikiLink) },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HelpCenter,
                            contentDescription = MR.strings.wiki.stable,
                        )
                    }
                }
            )
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .testTag(TestTag.List)
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            SiteId(viewState.siteId, onEvent)
            CustomDivider()

            ConnectionsItem(viewState.connectionsItemViewState, onEvent)
            CustomDivider()

            PipelineItem(viewState.pipelineItemViewState, onEvent)
            CustomDivider()

            MicDomainItem(viewState.micDomainItemViewState, onEvent)
            CustomDivider()

            WakeDomainItem(viewState.wakeDomainItemViewState, onEvent)
            CustomDivider()

            VadDomainItem(viewState.vadDomainItemViewState, onEvent)
            CustomDivider()

            AsrDomainItem(viewState.asrDomainItemViewState, onEvent)
            CustomDivider()

            IntentDomainItem(viewState.intentDomainItemViewState, onEvent)
            CustomDivider()

            HandleDomainItem(viewState.handleDomainItemViewState, onEvent)
            CustomDivider()

            TtsDomainItem(viewState.ttsDomainItemViewState, onEvent)
            CustomDivider()

            SndDomainItem(viewState.sndDomainItemViewState, onEvent)
            CustomDivider()

        }
    }
}

/**
 * site id element
 */
@Composable
private fun SiteId(
    viewState: SiteIdViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    TextFieldListItem(
        modifier = Modifier.testTag(TestTag.ConfigurationSiteId),
        value = viewState.text.collectAsState().value,
        onValueChange = { onEvent(SiteIdChange(it)) },
        label = MR.strings.siteId.stable,
    )

}

/**
 * Go to connections screen
 */
@Composable
private fun ConnectionsItem(
    connectionsItemViewState: ConnectionsItemViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(ConnectionsConfigurationScreen)) }
            .testTag(ConnectionsConfigurationScreen),
        text = { Text(MR.strings.connections.stable) },
        secondaryText = { Text(MR.strings.connections_information.stable) },
        trailing = if (connectionsItemViewState.hasError) {
            { EventStateIconTinted(ErrorState.Error(MR.strings.error.stable)) }
        } else null
    )

}

/**
 * List element for dialog management setting
 * shows which option is active
 */
@Composable
private fun PipelineItem(
    viewState: PipelineItemViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(DialogManagementConfigurationScreen)) }
            .testTag(DialogManagementConfigurationScreen),
        text = { Text(MR.strings.dialog_pipeline.stable) },
        secondaryText = { Text(viewState.dialogManagementOption.text) },
    )

}

/**
 * List element for dialog management setting
 * shows which option is active
 */
@Composable
private fun MicDomainItem(
    viewState: MicDomainItemViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    //TODO some information in secondary
    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(AudioInputConfigurationScreen)) }
            .testTag(AudioInputConfigurationScreen),
        text = { Text(MR.strings.audio_input.stable) },
        secondaryText = { Text(MR.strings.audio_input_information.stable) },
    )

}

/**
 * List element for wake word configuration
 * shows which option is selected
 */
@Composable
private fun WakeDomainItem(
    viewState: WakeDomainItemViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(WakeWordConfigurationScreen)) }
            .testTag(WakeWordConfigurationScreen),
        text = { Text(MR.strings.wakeWord.stable) },
        secondaryText = { Text(viewState.wakeWordValueOption.text) },
    )

}

/**
 * List element for speech to text configuration
 * shows which option is selected
 */
@Composable
private fun AsrDomainItem(
    viewState: AsrDomainItemViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(SpeechToTextConfigurationScreen)) }
            .testTag(SpeechToTextConfigurationScreen),
        text = { Text(MR.strings.speechToText.stable) },
        secondaryText = { Text(viewState.speechToTextOption.text) },
    )

}

/**
 * List element for speech to text configuration
 * shows which option is selected
 */
@Composable
private fun VadDomainItem(
    viewState: VadDomainItemViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(VoiceActivityDetectionConfigurationScreen)) }
            .testTag(VoiceActivityDetectionConfigurationScreen),
        text = { Text(MR.strings.voice_activity_detection.stable) },
        secondaryText = { Text(viewState.voiceActivityDetectionOption.text) },
    )

}


/**
 * List element for intent recognition configuration
 * shows which option is selected
 */
@Composable
private fun IntentDomainItem(
    viewState: IntentDomainItemViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(IntentRecognitionConfigurationScreen)) }
            .testTag(IntentRecognitionConfigurationScreen),
        text = { Text(MR.strings.intentRecognition.stable) },
        secondaryText = { Text(viewState.intentRecognitionOption.text) },
    )

}

/**
 * List element for text to speech configuration
 * shows which option is selected
 */
@Composable
private fun TtsDomainItem(
    viewState: TtsDomainItemViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(TextToSpeechConfigurationScreen)) }
            .testTag(TextToSpeechConfigurationScreen),
        text = { Text(MR.strings.textToSpeech.stable) },
        secondaryText = { Text(viewState.textToSpeechOption.text) },
    )

}

/**
 * List element for audio playing configuration
 * shows which option is selected
 */
@Composable
private fun SndDomainItem(
    viewState: SndDomainItemViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(AudioPlayingConfigurationScreen)) }
            .testTag(AudioPlayingConfigurationScreen),
        text = { Text(MR.strings.audioPlaying.stable) },
        secondaryText = { Text(viewState.audioPlayingOption.text) },
    )

}

/**
 * List element for intent handling configuration
 * shows which option is selected
 */
@Composable
private fun HandleDomainItem(
    viewState: HandleDomainItemViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(IntentHandlingConfigurationScreen)) }
            .testTag(IntentHandlingConfigurationScreen),
        text = { Text(MR.strings.intentHandling.stable) },
        secondaryText = { Text(viewState.intentHandlingOption.text) },
    )

}