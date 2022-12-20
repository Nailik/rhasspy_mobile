package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.getViewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.configuration.content.porcupine.PorcupineKeywordScreen
import org.rhasspy.mobile.android.configuration.content.porcupine.PorcupineLanguageScreen
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.elements.translate
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItemVisibility
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.permissions.RequiresMicrophonePermission
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel

enum class WakeWordConfigurationScreens {
    Overview,
    PorcupineKeyword,
    PorcupineLanguage
}

/**
 * Nav Host of Wake word configuration screens
 */
@Preview
@Composable
fun WakeWordConfigurationContent(viewModel: WakeWordConfigurationViewModel = getViewModel()) {

    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = WakeWordConfigurationScreens.Overview.name
        ) {

            composable(WakeWordConfigurationScreens.Overview.name) {
                WakeWordConfigurationOverview(viewModel)
            }

            composable(WakeWordConfigurationScreens.PorcupineLanguage.name) {
                PorcupineLanguageScreen(viewModel)
            }

            composable(WakeWordConfigurationScreens.PorcupineKeyword.name) {
                PorcupineKeywordScreen(viewModel)
            }

        }
    }

}

/**
 * Overview to configure wake word
 * Drop Down of option
 * porcupine wake word settings
 */
@Composable
private fun WakeWordConfigurationOverview(viewModel: WakeWordConfigurationViewModel) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.WakeWordConfiguration),
        title = MR.strings.wakeWord,
        viewModel = viewModel,
        testContent = { TestContent(viewModel) }
    ) {

        item {
            //drop down list to select option
            RadioButtonsEnumSelection(
                modifier = Modifier.testTag(TestTag.WakeWordOptions),
                selected = viewModel.wakeWordOption.collectAsState().value,
                onSelect = viewModel::selectWakeWordOption,
                values = viewModel.wakeWordOptions
            ) {

                if (viewModel.isWakeWordPorcupineSettingsVisible(it)) {
                    PorcupineConfiguration(viewModel)
                }

                if (viewModel.isUdpOutputSettingsVisible(it)) {
                    UdpSettings(viewModel)
                }

            }
        }
    }

}

/**
 * configuration for porcupine hot word
 * picovoice console for token
 * file option
 * language selection
 * sensitivity slider
 */
@Composable
private fun PorcupineConfiguration(viewModel: WakeWordConfigurationViewModel) {

    Column(
        modifier = Modifier
            .testTag(TestTag.PorcupineWakeWordSettings)
            .padding(ContentPaddingLevel1)
    ) {

        //porcupine access token
        TextFieldListItemVisibility(
            modifier = Modifier.testTag(TestTag.PorcupineAccessToken),
            value = viewModel.wakeWordPorcupineAccessToken.collectAsState().value,
            onValueChange = viewModel::updateWakeWordPorcupineAccessToken,
            label = MR.strings.porcupineAccessKey
        )

        //button to open picovoice console to generate access token
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineOpenConsole)
                .clickable(onClick = viewModel::openPicoVoiceConsole),
            icon = {
                Icon(
                    imageVector = Icons.Filled.Link,
                    contentDescription = MR.strings.openPicoVoiceConsole
                )
            },
            text = { Text(MR.strings.openPicoVoiceConsole) },
            secondaryText = { Text(MR.strings.openPicoVoiceConsoleInfo) }
        )

        //opens page for porcupine keyword or language selection
        val navigation = LocalNavController.current

        //opens page for porcupine language selection
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineLanguage)
                .clickable { navigation.navigate(WakeWordConfigurationScreens.PorcupineLanguage.name) },
            text = { Text(MR.strings.language) },
            secondaryText = { Text(viewModel.wakeWordPorcupineLanguage.collectAsState().value.text) }
        )

        //wake word list
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineKeyword)
                .clickable { navigation.navigate(WakeWordConfigurationScreens.PorcupineKeyword.name) },
            text = { Text(MR.strings.wakeWord) },
            secondaryText = { Text("${viewModel.wakeWordPorcupineKeywordCount.collectAsState().value} ${translate(MR.strings.active)}") }
        )

    }

}

/**
 *  udp host and port
 */
@Composable
private fun UdpSettings(viewModel: WakeWordConfigurationViewModel) {

    Column {

        //udp host
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.AudioRecordingUdpHost),
            label = MR.strings.host,
            value = viewModel.udpOutputHost.collectAsState().value,
            onValueChange = viewModel::changeUdpOutputHost
        )

        //udp port
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.AudioRecordingUdpPort),
            label = MR.strings.port,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = viewModel.udpOutputPortText.collectAsState().value,
            onValueChange = viewModel::changeUdpOutputPort
        )

    }

}

/**
 * test button to start wakeword detection test
 */
@Composable
private fun TestContent(
    viewModel: WakeWordConfigurationViewModel
) {
    RequiresMicrophonePermission(MR.strings.defaultText, viewModel::runTest) { onClick ->
        FilledTonalButtonListItem(
            text = MR.strings.startRecordAudio,
            onClick = onClick
        )
    }
}