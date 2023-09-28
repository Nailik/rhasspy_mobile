package org.rhasspy.mobile.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.StableStringResource
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
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination.ConfigurationScreen
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.OpenWikiLink
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change.SiteIdChange
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

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
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            if (viewState.hasError.collectAsState().value) {
                ServiceErrorInformation()
            }

            Column(
                modifier = Modifier
                    .testTag(TestTag.List)
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {

                SiteId(viewState.siteId, onEvent)
                CustomDivider()

                Connections(viewState.connectionsViewState, onEvent)
                CustomDivider()

                DialogManagement(viewState.dialogPipeline, onEvent)
                CustomDivider()

                AudioInput(viewState.audioInput, onEvent)
                CustomDivider()

                WakeWord(viewState.wakeWord, onEvent)
                CustomDivider()

                SpeechToText(viewState.speechToText, onEvent)
                CustomDivider()

                VoiceActivityDetection(viewState.voiceActivityDetection, onEvent)
                CustomDivider()

                IntentRecognition(viewState.intentRecognition, onEvent)
                CustomDivider()

                TextToSpeech(viewState.textToSpeech, onEvent)
                CustomDivider()

                AudioPlaying(viewState.audioPlaying, onEvent)
                CustomDivider()

                IntentHandling(viewState.intentHandling, onEvent)
                CustomDivider()

            }
        }

    }
}

/**
 * error information for service
 */
@Composable
private fun ServiceErrorInformation() {

    Surface {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
                .padding(horizontal = 16.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = MR.strings.error.stable,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    resource = MR.strings.error.stable,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
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
private fun Connections(
    connectionsViewState: ConnectionsViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(ConnectionsConfigurationScreen)) }
            .testTag(ConnectionsConfigurationScreen),
        text = { Text(MR.strings.connections.stable) },
        secondaryText = { Text(MR.strings.connections_information.stable) },
        trailing = if (connectionsViewState.hasError) {
            { EventStateIconTinted(ErrorState.Exception()) }
        } else null
    )

}

/**
 * List element for dialog management setting
 * shows which option is active
 */
@Composable
private fun DialogManagement(
    viewState: DialogPipelineViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.dialog_pipeline.stable,
        secondaryText = viewState.dialogManagementOption.text,
        serviceViewState = viewState.serviceState,
        destination = DialogManagementConfigurationScreen,
        onEvent = onEvent
    )

}

/**
 * List element for dialog management setting
 * shows which option is active
 */
@Composable
private fun AudioInput(
    viewState: AudioInputViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.audio_input.stable,
        secondaryText = MR.strings.audio_input_information.stable,
        serviceViewState = viewState.serviceState,
        destination = AudioInputConfigurationScreen,
        onEvent = onEvent
    )

}

/**
 * List element for wake word configuration
 * shows which option is selected
 */
@Composable
private fun WakeWord(
    viewState: WakeWordViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.wakeWord.stable,
        secondaryText = viewState.wakeWordValueOption.text,
        serviceViewState = viewState.serviceState,
        destination = WakeWordConfigurationScreen,
        onEvent = onEvent
    )

}

/**
 * List element for speech to text configuration
 * shows which option is selected
 */
@Composable
private fun SpeechToText(
    viewState: SpeechToTextViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.speechToText.stable,
        secondaryText = viewState.speechToTextOption.text,
        serviceViewState = viewState.serviceState,
        destination = SpeechToTextConfigurationScreen,
        onEvent = onEvent
    )

}

/**
 * List element for speech to text configuration
 * shows which option is selected
 */
@Composable
private fun VoiceActivityDetection(
    viewState: VoiceActivityDetectionViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.voice_activity_detection.stable,
        secondaryText = viewState.voiceActivityDetectionOption.text,
        serviceViewState = viewState.serviceState,
        destination = VoiceActivityDetectionConfigurationScreen,
        onEvent = onEvent
    )

}


/**
 * List element for intent recognition configuration
 * shows which option is selected
 */
@Composable
private fun IntentRecognition(
    viewState: IntentRecognitionViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.intentRecognition.stable,
        secondaryText = viewState.intentRecognitionOption.text,
        serviceViewState = viewState.serviceState,
        destination = IntentRecognitionConfigurationScreen,
        onEvent = onEvent
    )

}

/**
 * List element for text to speech configuration
 * shows which option is selected
 */
@Composable
private fun TextToSpeech(
    viewState: TextToSpeechViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.textToSpeech.stable,
        secondaryText = viewState.textToSpeechOption.text,
        serviceViewState = viewState.serviceState,
        destination = TextToSpeechConfigurationScreen,
        onEvent = onEvent
    )

}

/**
 * List element for audio playing configuration
 * shows which option is selected
 */
@Composable
private fun AudioPlaying(
    viewState: AudioPlayingViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.audioPlaying.stable,
        secondaryText = viewState.audioPlayingOption.text,
        serviceViewState = viewState.serviceState,
        destination = AudioPlayingConfigurationScreen,
        onEvent = onEvent
    )

}

/**
 * List element for intent handling configuration
 * shows which option is selected
 */
@Composable
private fun IntentHandling(
    viewState: IntentHandlingViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.intentHandling.stable,
        secondaryText = viewState.intentHandlingOption.text,
        serviceViewState = viewState.serviceState,
        destination = IntentHandlingConfigurationScreen,
        onEvent = onEvent
    )

}

/**
 * list item
 */
@Composable
private fun ConfigurationListItem(
    text: StableStringResource,
    secondaryText: StableStringResource,
    serviceViewState: ServiceViewState? = null,
    destination: ConfigurationScreenNavigationDestination,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {
    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(destination)) }
            .testTag(destination),
        text = { Text(text) },
        secondaryText = { Text(secondaryText) },
        trailing = serviceViewState?.let {
            {
                val serviceStateValue by serviceViewState.connectionState.collectAsState()
                EventStateIconTinted(serviceStateValue)
            }
        }
    )

}