package org.rhasspy.mobile.android.screens.bottomnavigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.screens.LocalNavController
import org.rhasspy.mobile.android.screens.mainNavigation.configuration.AudioPlayingConfigurationContent
import org.rhasspy.mobile.android.screens.mainNavigation.configuration.AudioRecordingConfigurationContent
import org.rhasspy.mobile.android.screens.mainNavigation.configuration.DialogManagementConfigurationContent
import org.rhasspy.mobile.android.screens.mainNavigation.configuration.IntentHandlingConfigurationContent
import org.rhasspy.mobile.android.screens.mainNavigation.configuration.IntentRecognitionConfigurationContent
import org.rhasspy.mobile.android.screens.mainNavigation.configuration.MqttConfigurationContent
import org.rhasspy.mobile.android.screens.mainNavigation.configuration.RemoteHermesHttpConfigurationContent
import org.rhasspy.mobile.android.screens.mainNavigation.configuration.SpeechToTextConfigurationContent
import org.rhasspy.mobile.android.screens.mainNavigation.configuration.TextToSpeechConfigurationContent
import org.rhasspy.mobile.android.screens.mainNavigation.configuration.WakeWordConfigurationContent
import org.rhasspy.mobile.android.screens.mainNavigation.configuration.WebServerConfigurationContent
import org.rhasspy.mobile.android.utils.CustomDivider
import org.rhasspy.mobile.android.utils.ListElement
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.android.utils.toText
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

enum class ConfigurationScreens {
    ConfigurationList,
    AudioPlayingConfiguration,
    AudioRecordingConfiguration,
    DialogManagementConfiguration,
    IntentHandlingConfiguration,
    IntentRecognitionConfiguration,
    MqttConfiguration,
    RemoteHermesHttpConfiguration,
    SpeechToTextConfiguration,
    TextToSpeechConfiguration,
    WakeWordConfiguration,
    WebServerConfiguration
}

/**
 * configuration screens with list items that open bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ConfigurationListScreen() {

    val navController = rememberNavController()


    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AppBar(scrollBehavior)
            },
        ) { paddingValues ->

            NavHost(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                startDestination = ConfigurationScreens.ConfigurationList.name
            ) {

                composable(ConfigurationScreens.ConfigurationList.name) {
                    ConfigurationList()
                }

                composable(ConfigurationScreens.AudioPlayingConfiguration.name) {
                    AudioPlayingConfigurationContent()
                }

                composable(ConfigurationScreens.AudioRecordingConfiguration.name) {
                    AudioRecordingConfigurationContent()
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

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(scrollBehavior: TopAppBarScrollBehavior) {

    var currentDestination by remember { mutableStateOf(ConfigurationScreens.ConfigurationList) }
    val navController = LocalNavController.current

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = ConfigurationScreens.valueOf(destination.route ?: ConfigurationScreens.ConfigurationList.name)
        }
    }

    val title = when (currentDestination) {
        ConfigurationScreens.ConfigurationList -> MR.strings.configuration
        ConfigurationScreens.AudioPlayingConfiguration -> MR.strings.audioPlaying
        ConfigurationScreens.AudioRecordingConfiguration -> MR.strings.audioRecording
        ConfigurationScreens.DialogManagementConfiguration -> MR.strings.dialogueManagement
        ConfigurationScreens.IntentHandlingConfiguration -> MR.strings.intentHandling
        ConfigurationScreens.IntentRecognitionConfiguration -> MR.strings.intentRecognition
        ConfigurationScreens.MqttConfiguration -> MR.strings.mqtt
        ConfigurationScreens.RemoteHermesHttpConfiguration -> MR.strings.remoteHermesHTTP
        ConfigurationScreens.SpeechToTextConfiguration -> MR.strings.speechToText
        ConfigurationScreens.TextToSpeechConfiguration -> MR.strings.textToSpeech
        ConfigurationScreens.WakeWordConfiguration -> MR.strings.wakeWord
        ConfigurationScreens.WebServerConfiguration -> MR.strings.webserver
    }

    MediumTopAppBar(
        title = {
            Text(title)
        },
        scrollBehavior = scrollBehavior
    )
}

@Preview
@Composable
private fun ConfigurationList(viewModel: ConfigurationScreenViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        SiteId(viewModel)
        CustomDivider()

        Webserver(viewModel)
        CustomDivider()

        RemoteHermesHttp(viewModel)
        CustomDivider()

        Mqtt(viewModel)
        CustomDivider()

        AudioRecording(viewModel)
        CustomDivider()

        WakeWord(viewModel)
        CustomDivider()

        SpeechToText(viewModel)
        CustomDivider()

        IntentRecognition(viewModel)
        CustomDivider()

        TextToSpeech(viewModel)
        CustomDivider()

        AudioPlaying(viewModel)
        CustomDivider()

        DialogManagement(viewModel)
        CustomDivider()

        IntentHandling(viewModel)
        CustomDivider()

    }
}


/**
 * site id element
 */
@Composable
private fun SiteId(viewModel: ConfigurationScreenViewModel) {

    TextFieldListItem(
        value = viewModel.siteId.collectAsState().value,
        onValueChange = viewModel::changeSiteId,
        label = MR.strings.siteId,
        paddingValues = PaddingValues(top = 4.dp, bottom = 16.dp),
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
        screen = ConfigurationScreens.WebServerConfiguration
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
        secondaryText = "${translate(MR.strings.sslValidation)} ${viewModel.isHttpSSLVerificationEnabled.collectAsState().value.toText()}",
        screen = ConfigurationScreens.RemoteHermesHttpConfiguration
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
        screen = ConfigurationScreens.MqttConfiguration
    )

}

/**
 * List element for audio recording configuration
 * shows if udp audio output is turned on
 */
@Composable
private fun AudioRecording(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.audioRecording,
        secondaryText = if (viewModel.isUdpOutputEnabled.collectAsState().value) {
            MR.strings.udpAudioOutputOn
        } else {
            MR.strings.udpAudioOutputOff
        },
        screen = ConfigurationScreens.AudioRecordingConfiguration
    )

}


/**
 * List element for wake wordc onfiguration
 * shows which option is selected
 */
@Composable
private fun WakeWord(viewModel: ConfigurationScreenViewModel) {

    val wakeWordValueOption = viewModel.wakeWordOption.collectAsState().value

    ConfigurationListItem(
        text = MR.strings.wakeWord,
        secondaryText = wakeWordValueOption.text,
        screen = ConfigurationScreens.WakeWordConfiguration
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
        screen = ConfigurationScreens.SpeechToTextConfiguration
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
        screen = ConfigurationScreens.IntentRecognitionConfiguration
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
        screen = ConfigurationScreens.TextToSpeechConfiguration
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
        screen = ConfigurationScreens.AudioPlayingConfiguration
    )

}

/**
 * List element for dialog management setting
 * shows which option is active
 */
@Composable
private fun DialogManagement(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.dialogueManagement,
        secondaryText = viewModel.dialogueManagementOption.collectAsState().value.text,
        screen = ConfigurationScreens.DialogManagementConfiguration
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
        screen = ConfigurationScreens.IntentHandlingConfiguration
    )

}


@Composable
private fun ConfigurationListItem(
    text: StringResource,
    secondaryText: StringResource,
    screen: ConfigurationScreens
) {
    val navController = LocalNavController.current

    ListElement(
        modifier = Modifier.clickable {
            navController.navigate(screen.name)
        },
        text = { Text(text) },
        secondaryText = { Text(secondaryText) }
    )
}

@Composable
private fun ConfigurationListItem(
    text: StringResource,
    secondaryText: String,
    @Suppress("SameParameterValue") screen: ConfigurationScreens
) {
    val navController = LocalNavController.current

    ListElement(
        modifier = Modifier.clickable {
            navController.navigate(screen.name)
        },
        text = { Text(text) },
        secondaryText = { Text(text = secondaryText) }
    )
}