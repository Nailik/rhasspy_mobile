package org.rhasspy.mobile.android.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.UiEventEffect
import org.rhasspy.mobile.android.configuration.content.*
import org.rhasspy.mobile.android.content.elements.*
import org.rhasspy.mobile.android.content.item.EventStateIconTinted
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiAction
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiAction.ScrollToError
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiAction.SiteIdChange
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.AudioPlayingViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.DialogManagementViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.IntentHandlingViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.IntentRecognitionViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.MqttViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.RemoteHermesHttpViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.SiteIdViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.SpeechToTextViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.TextToSpeechViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.WakeWordViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.WebServerViewState

/**
 * configuration screens with list items that open bottom sheet
 */
@Preview
@Composable
fun ConfigurationScreen(
    viewModel: ConfigurationScreenViewModel = get(),
    scrollToError: Boolean = false
) {

    val viewState by viewModel.viewState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(MR.strings.configuration) }
            )
        },
    ) { paddingValues ->

        val hasError by viewState.hasError.collectAsState()
        val scrollState = rememberLazyListState()

        UiEventEffect(
            event = viewState.scrollToErrorEvent,
            onConsumed = viewModel::onConsumed
        ) {
            //+1 to account for sticky header
            scrollState.animateScrollToItem(it.firstErrorIndex + 1)
        }

        LazyColumn(
            modifier = Modifier
                .testTag(TestTag.List)
                .padding(paddingValues)
                .fillMaxSize(),
            state = scrollState
        ) {

            if (hasError) {
                stickyHeader {
                    ServiceErrorInformation(viewModel::onAction)
                }
            }

            item {
                SiteId(viewState.siteId, viewModel::onAction)
                CustomDivider()
            }

            item {
                RemoteHermesHttp(viewState.remoteHermesHttp)
                CustomDivider()
            }

            item {
                Webserver(viewState.webserver)
                CustomDivider()
            }

            item {
                Mqtt(viewState.mqtt)
                CustomDivider()
            }

            item {
                WakeWord(viewState.wakeWord)
                CustomDivider()
            }

            item {
                SpeechToText(viewState.speechToText)
                CustomDivider()
            }

            item {
                IntentRecognition(viewState.intentRecognition)
                CustomDivider()
            }

            item {
                TextToSpeech(viewState.textToSpeech)
                CustomDivider()
            }

            item {
                AudioPlaying(viewState.audioPlaying)
                CustomDivider()
            }

            item {
                DialogManagement(viewState.dialogManagement)
                CustomDivider()
            }

            item {
                IntentHandling(viewState.intentHandling)
                CustomDivider()
            }

        }
    }

}

/**
 * add sub screens to main navigation
 */
fun NavGraphBuilder.addConfigurationScreens() {

    composable(ConfigurationScreenType.AudioPlayingConfiguration.route) {
        AudioPlayingConfigurationContent()
    }

    composable(ConfigurationScreenType.DialogManagementConfiguration.route) {
        DialogManagementConfigurationContent()
    }

    composable(ConfigurationScreenType.IntentHandlingConfiguration.route) {
        IntentHandlingConfigurationContent()
    }

    composable(ConfigurationScreenType.IntentRecognitionConfiguration.route) {
        IntentRecognitionConfigurationContent()
    }

    composable(ConfigurationScreenType.MqttConfiguration.route) {
        MqttConfigurationContent()
    }

    composable(ConfigurationScreenType.RemoteHermesHttpConfiguration.route) {
        RemoteHermesHttpConfigurationContent()
    }

    composable(ConfigurationScreenType.SpeechToTextConfiguration.route) {
        SpeechToTextConfigurationContent()
    }

    composable(ConfigurationScreenType.TextToSpeechConfiguration.route) {
        TextToSpeechConfigurationContent()
    }

    composable(ConfigurationScreenType.WakeWordConfiguration.route) {
        WakeWordConfigurationContent()
    }

    composable(ConfigurationScreenType.WebServerConfiguration.route) {
        WebServerConfigurationContent()
    }

}

/**
 * error information for service
 */
@Composable
private fun ServiceErrorInformation(
    onAction: (ConfigurationScreenUiAction) -> Unit
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
            onClick = { onAction(ScrollToError) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = MR.strings.error,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    resource = MR.strings.error,
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
    onAction: (ConfigurationScreenUiAction) -> Unit
) {

    val value by viewState.text.collectAsState()

    TextFieldListItem(
        modifier = Modifier.testTag(TestTag.ConfigurationSiteId),
        value = value,
        onValueChange = { onAction(SiteIdChange(it)) },
        label = MR.strings.siteId,
    )

}

/**
 * List element for http configuration
 * shows if ssl verification is enabled
 */
@Composable
private fun RemoteHermesHttp(viewState: RemoteHermesHttpViewState) {

    ConfigurationListItem(
        text = MR.strings.remoteHermesHTTP,
        secondaryText = "${translate(MR.strings.sslValidation)} ${translate(viewState.isHttpSSLVerificationEnabled.not().toText())}",
        screen = ConfigurationScreenType.RemoteHermesHttpConfiguration,
        serviceState = viewState.serviceState
    )

}

/**
 * List element for text to speech configuration
 * shows if web server is enabled
 */
@Composable
private fun Webserver(viewState: WebServerViewState) {

    ConfigurationListItem(
        text = MR.strings.webserver,
        secondaryText = viewState.isHttpServerEnabled.toText(),
        screen = ConfigurationScreenType.WebServerConfiguration,
        serviceState = viewState.serviceState
    )

}

/**
 * List element for mqtt configuration
 * shows connection state of mqtt
 */
@Composable
private fun Mqtt(viewState: MqttViewState) {

    val isMQTTConnected by viewState.isMQTTConnected.collectAsState()

    ConfigurationListItem(
        text = MR.strings.mqtt,
        secondaryText = if (isMQTTConnected) MR.strings.connected else MR.strings.notConnected,
        screen = ConfigurationScreenType.MqttConfiguration,
        serviceState = viewState.serviceState
    )

}


/**
 * List element for wake word configuration
 * shows which option is selected
 */
@Composable
private fun WakeWord(viewState: WakeWordViewState) {

    ConfigurationListItem(
        text = MR.strings.wakeWord,
        secondaryText = viewState.wakeWordValueOption.text,
        screen = ConfigurationScreenType.WakeWordConfiguration,
        serviceState = viewState.serviceState
    )

}

/**
 * List element for speech to text configuration
 * shows which option is selected
 */
@Composable
private fun SpeechToText(viewState: SpeechToTextViewState) {

    ConfigurationListItem(
        text = MR.strings.speechToText,
        secondaryText = viewState.speechToTextOption.text,
        screen = ConfigurationScreenType.SpeechToTextConfiguration,
        serviceState = viewState.serviceState
    )

}


/**
 * List element for intent recognition configuration
 * shows which option is selected
 */
@Composable
private fun IntentRecognition(viewState: IntentRecognitionViewState) {

    ConfigurationListItem(
        text = MR.strings.intentRecognition,
        secondaryText = viewState.intentRecognitionOption.text,
        screen = ConfigurationScreenType.IntentRecognitionConfiguration,
        serviceState = viewState.serviceState
    )

}

/**
 * List element for text to speech configuration
 * shows which option is selected
 */
@Composable
private fun TextToSpeech(viewState: TextToSpeechViewState) {

    ConfigurationListItem(
        text = MR.strings.textToSpeech,
        secondaryText = viewState.textToSpeechOption.text,
        screen = ConfigurationScreenType.TextToSpeechConfiguration,
        serviceState = viewState.serviceState
    )

}

/**
 * List element for audio playing configuration
 * shows which option is selected
 */
@Composable
private fun AudioPlaying(viewState: AudioPlayingViewState) {

    ConfigurationListItem(
        text = MR.strings.audioPlaying,
        secondaryText = viewState.audioPlayingOption.text,
        screen = ConfigurationScreenType.AudioPlayingConfiguration,
        serviceState = viewState.serviceState
    )

}

/**
 * List element for dialog management setting
 * shows which option is active
 */
@Composable
private fun DialogManagement(viewState: DialogManagementViewState) {

    ConfigurationListItem(
        text = MR.strings.dialogManagement,
        secondaryText = viewState.dialogManagementOption.text,
        screen = ConfigurationScreenType.DialogManagementConfiguration,
        serviceState = viewState.serviceState
    )

}

/**
 * List element for intent handling configuration
 * shows which option is selected
 */
@Composable
private fun IntentHandling(viewState: IntentHandlingViewState) {

    ConfigurationListItem(
        text = MR.strings.intentHandling,
        secondaryText = viewState.intentHandlingOption.text,
        screen = ConfigurationScreenType.IntentHandlingConfiguration,
        serviceState = viewState.serviceState
    )

}

/**
 * list item
 */
@Composable
private fun ConfigurationListItem(
    text: StringResource,
    secondaryText: StringResource,
    screen: ConfigurationScreenType,
    serviceState: StateFlow<ServiceState>
) {

    val navController = LocalMainNavController.current

    ListElement(
        modifier = Modifier
            .clickable {
                navController.navigate(screen.route)
            }
            .testTag(screen),
        text = { Text(text) },
        secondaryText = { Text(secondaryText) },
        trailing = {
            val serviceStateValue by serviceState.collectAsState()
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
    text: StringResource,
    secondaryText: String,
    screen: ConfigurationScreenType,
    serviceState: StateFlow<ServiceState>
) {

    val navController = LocalMainNavController.current

    ListElement(
        modifier = Modifier
            .clickable {
                navController.navigate(screen.route)
            }
            .testTag(screen),
        text = { Text(text) },
        secondaryText = { Text(text = secondaryText) },
        trailing = {
            val serviceStateValue by serviceState.collectAsState()
            EventStateIconTinted(serviceStateValue)
        }
    )

}