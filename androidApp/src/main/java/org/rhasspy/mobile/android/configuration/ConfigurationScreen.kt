package org.rhasspy.mobile.android.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.content.*
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * configuration screens with list items that open bottom sheet
 */
@Preview
@Composable
fun ConfigurationScreen(viewModel: ConfigurationScreenViewModel = viewModel()) {

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
        secondaryText = "${translate(MR.strings.sslValidation)} ${translate(viewModel.isHttpSSLVerificationEnabled.collectAsState().value.toText())}",
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
 * List element for wake word configuration
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
        text = MR.strings.dialogManagement,
        secondaryText = viewModel.dialogManagementOption.collectAsState().value.text,
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

/**
 * list item
 */
@Composable
private fun ConfigurationListItem(
    text: StringResource,
    secondaryText: StringResource,
    screen: ConfigurationScreens
) {

    val navController = LocalMainNavController.current

    ListElement(
        modifier = Modifier
            .clickable {
                navController.navigate(screen.name)
            }
            .testTag(screen),
        text = { Text(text) },
        secondaryText = { Text(secondaryText) }
    )

}

/**
 * list item
 */
@Composable
private fun ConfigurationListItem(
    text: StringResource,
    secondaryText: String,
    @Suppress("SameParameterValue") screen: ConfigurationScreens
) {

    val navController = LocalMainNavController.current

    ListElement(
        modifier = Modifier
            .clickable {
                navController.navigate(screen.name)
            }
            .testTag(screen),
        text = { Text(text) },
        secondaryText = { Text(text = secondaryText) }
    )

}