package org.rhasspy.mobile.ui.main

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.*
import org.rhasspy.mobile.ui.content.item.EventStateIconTinted
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination.ConfigurationScreen
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change.SiteIdChange
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Consumed.ScrollToError
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

/**
 * configuration screens with list items that open bottom sheet
 */
@Composable
fun ConfigurationScreen() {

    val viewModel: ConfigurationScreenViewModel = LocalViewModelFactory.current.getViewModel()
    val scrollState = rememberScrollState()

    Screen(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        ConfigurationScreenContent(
            scrollState = scrollState,
            onEvent = viewModel::onEvent,
            viewState = viewState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreenContent(
    scrollState: ScrollState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit,
    viewState: ConfigurationScreenViewState
) {

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

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(viewState.scrollToError) {
            viewState.scrollToError?.also { index ->
                coroutineScope.launch {
                    scrollState.animateScrollTo(index * 2 + 2) //duplicate for divider
                    onEvent(ScrollToError)
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            if (viewState.hasError.collectAsState().value) {
                ServiceErrorInformation(onEvent)
            }

            Column(
                modifier = Modifier
                    .testTag(TestTag.List)
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {

                SiteId(viewState.siteId, onEvent)
                CustomDivider()

                RemoteHermesHttp(viewState.remoteHermesHttp, onEvent)
                CustomDivider()


                Webserver(viewState.webserver, onEvent)
                CustomDivider()


                Mqtt(viewState.mqtt, onEvent)
                CustomDivider()


                WakeWord(viewState.wakeWord, onEvent)
                CustomDivider()


                SpeechToText(viewState.speechToText, onEvent)
                CustomDivider()


                IntentRecognition(viewState.intentRecognition, onEvent)
                CustomDivider()


                TextToSpeech(viewState.textToSpeech, onEvent)
                CustomDivider()


                AudioPlaying(viewState.audioPlaying, onEvent)
                CustomDivider()


                DialogManagement(viewState.dialogManagement, onEvent)
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceErrorInformation(
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    Surface {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
                .padding(horizontal = 16.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            onClick = { onEvent(ScrollToErrorClick) }
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
 * List element for http configuration
 * shows if ssl verification is enabled
 */
@Composable
private fun RemoteHermesHttp(
    viewState: RemoteHermesHttpViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.remoteHermesHTTP.stable,
        secondaryText = "${translate(MR.strings.sslValidation.stable)} ${
            translate(
                viewState.isHttpSSLVerificationEnabled.not().toText()
            )
        }",
        viewState = viewState.serviceState,
        destination = RemoteHermesHttpConfigurationScreen,
        onEvent = onEvent
    )

}

/**
 * List element for text to speech configuration
 * shows if web server is enabled
 */
@Composable
private fun Webserver(
    viewState: WebServerViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.webserver.stable,
        secondaryText = viewState.isHttpServerEnabled.toText(),
        serviceViewState = viewState.serviceState,
        destination = WebServerConfigurationScreen,
        onEvent = onEvent
    )

}

/**
 * List element for mqtt configuration
 * shows connection state of mqtt
 */
@Composable
private fun Mqtt(
    viewState: MqttViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.mqtt.stable,
        secondaryText = if (viewState.isMQTTConnected) MR.strings.connected.stable else MR.strings.notConnected.stable,
        serviceViewState = viewState.serviceState,
        destination = MqttConfigurationScreen,
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
 * List element for dialog management setting
 * shows which option is active
 */
@Composable
private fun DialogManagement(
    viewState: DialogManagementViewState,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {

    ConfigurationListItem(
        text = MR.strings.dialogManagement.stable,
        secondaryText = viewState.dialogManagementOption.text,
        serviceViewState = viewState.serviceState,
        destination = DialogManagementConfigurationScreen,
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
    serviceViewState: ServiceViewState,
    destination: ConfigurationScreenNavigationDestination,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {
    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(destination)) }
            .testTag(destination),
        text = { Text(text) },
        secondaryText = { Text(secondaryText) },
        trailing = {
            val serviceStateValue by serviceViewState.serviceState.collectAsState()
            EventStateIconTinted(serviceStateValue)
        }
    )

}

/**
 * list item
 */
@Suppress("SameParameterValue")
@Composable
private fun ConfigurationListItem(
    text: StableStringResource,
    secondaryText: String,
    viewState: ServiceViewState,
    destination: ConfigurationScreenNavigationDestination,
    onEvent: (ConfigurationScreenUiEvent) -> Unit
) {
    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(destination)) }
            .testTag(destination),
        text = { Text(text) },
        secondaryText = { Text(text = secondaryText) },
        trailing = {
            val serviceStateValue by viewState.serviceState.collectAsState()
            EventStateIconTinted(serviceStateValue)
        }
    )
}