package org.rhasspy.mobile.android.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.content.*
import org.rhasspy.mobile.android.content.elements.*
import org.rhasspy.mobile.android.content.item.EventStateIconTinted
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.middleware.EventState
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * configuration screens with list items that open bottom sheet
 */
@Preview
@Composable
fun ConfigurationScreen(viewModel: ConfigurationScreenViewModel = get(), scrollToError: Boolean = false) {

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val firstErrorIndex by viewModel.firstErrorIndex.collectAsState()

    LaunchedEffect(scrollToError) {
        if (scrollToError) {
            scrollToFirstError(
                firstErrorIndex = firstErrorIndex,
                coroutineScope = coroutineScope,
                scrollState = scrollState
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(MR.strings.configuration) }
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            state = scrollState
        ) {

            stickyHeader {
                ServiceErrorInformation(viewModel, scrollState)
            }

            item {
                SiteId(viewModel)
                CustomDivider()
            }

            item {
                RemoteHermesHttp(viewModel)
                CustomDivider()
            }

            item {
                Webserver(viewModel)
                CustomDivider()
            }

            item {
                Mqtt(viewModel)
                CustomDivider()
            }

            item {
                WakeWord(viewModel)
                CustomDivider()
            }

            item {
                SpeechToText(viewModel)
                CustomDivider()
            }

            item {
                IntentRecognition(viewModel)
                CustomDivider()
            }

            item {
                TextToSpeech(viewModel)
                CustomDivider()
            }

            item {
                AudioPlaying(viewModel)
                CustomDivider()
            }

            item {
                DialogManagement(viewModel)
                CustomDivider()
            }

            item {
                IntentHandling(viewModel)
                CustomDivider()
            }

        }
    }

}

/**
 * add sub screens to main navigation
 */
fun NavGraphBuilder.addConfigurationScreens() {

    composable(ConfigurationScreens.AudioPlayingConfiguration.name) {
        AudioPlayingConfigurationContent()
    }

    composable(ConfigurationScreens.DialogManagementConfiguration.name) {
        DialogManagementConfigurationContent()
    }

    composable(ConfigurationScreens.IntentHandlingConfiguration.name) {
        IntentHandlingConfigurationContent()
    }

    composable(ConfigurationScreens.IntentRecognitionConfiguration.name) {
        IntentRecognitionConfigurationContent()
    }

    composable(ConfigurationScreens.MqttConfiguration.name) {
        MqttConfigurationContent()
    }

    composable(ConfigurationScreens.RemoteHermesHttpConfiguration.name) {
        RemoteHermesHttpConfigurationContent()
    }

    composable(ConfigurationScreens.SpeechToTextConfiguration.name) {
        SpeechToTextConfigurationContent()
    }

    composable(ConfigurationScreens.TextToSpeechConfiguration.name) {
        TextToSpeechConfigurationContent()
    }

    composable(ConfigurationScreens.WakeWordConfiguration.name) {
        WakeWordConfigurationContent()
    }

    composable(ConfigurationScreens.WebServerConfiguration.name) {
        WebServerConfigurationContent()
    }

}

/**
 * error information for service
 */
@Composable
private fun ServiceErrorInformation(viewModel: ConfigurationScreenViewModel, scrollState: LazyListState) {

    val coroutineScope = rememberCoroutineScope()
    val firstErrorIndex by viewModel.firstErrorIndex.collectAsState()

    Surface {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
                .padding(horizontal = 16.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            onClick = {
                scrollToFirstError(
                    firstErrorIndex = firstErrorIndex,
                    coroutineScope = coroutineScope,
                    scrollState = scrollState
                )
            }
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
private fun SiteId(viewModel: ConfigurationScreenViewModel) {

    TextFieldListItem(
        modifier = Modifier.testTag(TestTag.ConfigurationSiteId),
        value = viewModel.siteId.collectAsState().value,
        onValueChange = viewModel::changeSiteId,
        label = MR.strings.siteId,
    )

}

/**
 * List element for http configuration
 * shows if ssl verification is enabled
 */
@Composable
private fun RemoteHermesHttp(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.remoteHermesHTTP,
        secondaryText = "${translate(MR.strings.sslValidation)} ${translate(viewModel.isHttpSSLVerificationEnabled.collectAsState().value.toText())}",
        screen = ConfigurationScreens.RemoteHermesHttpConfiguration,
        serviceState = viewModel.isHttpClientHasError.collectAsState().value
    )

}

/**
 * List element for text to speech configuration
 * shows if web server is enabled
 */
@Composable
private fun Webserver(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.webserver,
        secondaryText = viewModel.isHttpServerEnabled.collectAsState().value.toText(),
        screen = ConfigurationScreens.WebServerConfiguration,
        serviceState = viewModel.isHttpServerHasError.collectAsState().value
    )

}

/**
 * List element for mqtt configuration
 * shows connection state of mqtt
 */
@Composable
private fun Mqtt(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.mqtt,
        secondaryText = if (viewModel.isMQTTConnected.collectAsState().value) MR.strings.connected else MR.strings.notConnected,
        screen = ConfigurationScreens.MqttConfiguration,
        serviceState = viewModel.isMqttHasError.collectAsState().value
    )

}


/**
 * List element for wake word configuration
 * shows which option is selected
 */
@Composable
private fun WakeWord(viewModel: ConfigurationScreenViewModel) {

    val wakeWordValueOption = viewModel.wakeWordOption.collectAsState().value

    ConfigurationListItem(
        text = MR.strings.wakeWord,
        secondaryText = wakeWordValueOption.text,
        screen = ConfigurationScreens.WakeWordConfiguration,
        serviceState = viewModel.isWakeWordServiceHasError.collectAsState().value
    )

}

/**
 * List element for speech to text configuration
 * shows which option is selected
 */
@Composable
private fun SpeechToText(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.speechToText,
        secondaryText = viewModel.speechToTextOption.collectAsState().value.text,
        screen = ConfigurationScreens.SpeechToTextConfiguration,
        serviceState = viewModel.isSpeechToTextHasError.collectAsState().value
    )

}


/**
 * List element for intent recognition configuration
 * shows which option is selected
 */
@Composable
private fun IntentRecognition(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.intentRecognition,
        secondaryText = viewModel.intentRecognitionOption.collectAsState().value.text,
        screen = ConfigurationScreens.IntentRecognitionConfiguration,
        serviceState = viewModel.isIntentRecognitionHasError.collectAsState().value
    )

}

/**
 * List element for text to speech configuration
 * shows which option is selected
 */
@Composable
private fun TextToSpeech(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.textToSpeech,
        secondaryText = viewModel.textToSpeechOption.collectAsState().value.text,
        screen = ConfigurationScreens.TextToSpeechConfiguration,
        serviceState = viewModel.isTextToSpeechHasError.collectAsState().value
    )

}

/**
 * List element for audio playing configuration
 * shows which option is selected
 */
@Composable
private fun AudioPlaying(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.audioPlaying,
        secondaryText = viewModel.audioPlayingOption.collectAsState().value.text,
        screen = ConfigurationScreens.AudioPlayingConfiguration,
        serviceState = viewModel.isAudioPlayingHasError.collectAsState().value
    )

}

/**
 * List element for dialog management setting
 * shows which option is active
 */
@Composable
private fun DialogManagement(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.dialogManagement,
        secondaryText = viewModel.dialogManagementOption.collectAsState().value.text,
        screen = ConfigurationScreens.DialogManagementConfiguration,
        serviceState = viewModel.isDialogManagementHasError.collectAsState().value
    )

}

/**
 * List element for intent handling configuration
 * shows which option is selected
 */
@Composable
private fun IntentHandling(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.intentHandling,
        secondaryText = viewModel.intentHandlingOption.collectAsState().value.text,
        screen = ConfigurationScreens.IntentHandlingConfiguration,
        serviceState = viewModel.isIntentHandlingHasError.collectAsState().value
    )

}

/**
 * list item
 */
@Composable
private fun ConfigurationListItem(
    text: StringResource,
    secondaryText: StringResource,
    screen: ConfigurationScreens,
    serviceState: EventState
) {

    val navController = LocalMainNavController.current

    ListElement(
        modifier = Modifier
            .clickable {
                navController.navigate(screen.name)
            }
            .testTag(screen),
        text = { Text(text) },
        secondaryText = { Text(secondaryText) },
        trailing = { EventStateIconTinted(serviceState) }
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
    screen: ConfigurationScreens,
    serviceState: EventState
) {

    val navController = LocalMainNavController.current

    ListElement(
        modifier = Modifier
            .clickable {
                navController.navigate(screen.name)
            }
            .testTag(screen),
        text = { Text(text) },
        secondaryText = { Text(text = secondaryText) },
        trailing = { EventStateIconTinted(serviceState) }
    )

}

/**
 * function to scroll to the first error
 */
private fun scrollToFirstError(
    firstErrorIndex: Int,
    coroutineScope: CoroutineScope,
    scrollState: LazyListState
) {
    coroutineScope.launch {
        if (firstErrorIndex in 0..10) {
            //+1 to account for sticky header
            scrollState.animateScrollToItem(firstErrorIndex + 1)
        }
    }
}