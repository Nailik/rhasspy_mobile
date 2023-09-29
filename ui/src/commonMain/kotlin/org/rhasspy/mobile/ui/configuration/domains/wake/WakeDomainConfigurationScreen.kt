package org.rhasspy.mobile.ui.configuration.domains.wake

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItemVisibility
import org.rhasspy.mobile.ui.main.SettingsScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.OpenPicoVoiceConsole
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.UpdateWakeWordPorcupineAccessToken
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordPorcupineConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordUdpConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.WakeWordConfigurationScreenDestination.EditPorcupineLanguageScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.WakeWordConfigurationScreenDestination.EditPorcupineWakeWordScreen

/**
 * Overview to configure wake word
 * Drop Down of option
 * porcupine wake word settings
 */
@Composable
fun WakeWordConfigurationOverviewScreen(viewModel: WakeWordConfigurationViewModel) {
    ScreenContent(
        screenViewModel = viewModel
    ) {
        SettingsScreenItemContent(
            title = MR.strings.wakeWord.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {

            val viewState by viewModel.viewState.collectAsState()


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                item {
                    WakeWordConfigurationOptionContent(
                        viewState = viewState,
                        onEvent = viewModel::onEvent
                    )
                }

            }

        }
    }
}

@Composable
private fun WakeWordConfigurationOptionContent(
    viewState: WakeWordConfigurationViewState,
    onEvent: (WakeWordConfigurationUiEvent) -> Unit
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.WakeWordOptions),
        selected = viewState.editData.wakeWordOption,
        onSelect = { onEvent(SelectWakeWordOption(it)) },
        values = viewState.editData.wakeWordOptions
    ) { option ->

        when (option) {
            WakeWordOption.Porcupine ->
                PorcupineConfiguration(
                    editData = viewState.editData.wakeWordPorcupineConfigurationData,
                    isMicrophonePermissionRequestVisible = viewState.isMicrophonePermissionRequestVisible,
                    onEvent = onEvent
                )

            WakeWordOption.Udp       ->
                UdpSettings(
                    editData = viewState.editData.wakeWordUdpConfigurationData,
                    isMicrophonePermissionRequestVisible = viewState.isMicrophonePermissionRequestVisible,
                    onEvent = onEvent
                )

            WakeWordOption.Rhasspy2HermesMQTT,
            WakeWordOption.Disabled  -> Unit
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
private fun PorcupineConfiguration(
    editData: WakeWordPorcupineConfigurationData,
    isMicrophonePermissionRequestVisible: Boolean,
    onEvent: (WakeWordConfigurationUiEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .testTag(TestTag.PorcupineWakeWordSettings)
            .padding(ContentPaddingLevel1)
    ) {

        //porcupine access token
        TextFieldListItemVisibility(
            label = MR.strings.porcupineAccessKey.stable,
            modifier = Modifier.testTag(TestTag.PorcupineAccessToken),
            value = editData.accessToken,
            onValueChange = { onEvent(UpdateWakeWordPorcupineAccessToken(it)) }
        )

        //button to open pico voice console to generate access token
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineOpenConsole)
                .clickable(onClick = { onEvent(OpenPicoVoiceConsole) }),
            icon = {
                Icon(
                    imageVector = Icons.Filled.Link,
                    contentDescription = MR.strings.openPicoVoiceConsole.stable
                )
            },
            text = { Text(MR.strings.openPicoVoiceConsole.stable) },
            secondaryText = { Text(MR.strings.openPicoVoiceConsoleInfo.stable) }
        )

        //opens page for porcupine language selection
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineLanguage)
                .clickable { onEvent(Navigate(EditPorcupineLanguageScreen)) },
            text = { Text(MR.strings.language.stable) },
            secondaryText = {
                val porcupineLanguageText by remember { derivedStateOf { editData.porcupineLanguage.text } }
                Text(porcupineLanguageText)
            }
        )

        //wake word list
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineKeyword)
                .clickable { onEvent(Navigate(EditPorcupineWakeWordScreen)) },
            text = { Text(MR.strings.wakeWord.stable) },
            secondaryText = { Text("${editData.keywordCount} ${translate(MR.strings.active.stable)}") }
        )

        //button to enabled microphone
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isMicrophonePermissionRequestVisible
        ) {
            FilledTonalButtonListItem(
                text = MR.strings.allowMicrophonePermission.stable,
                onClick = { onEvent(RequestMicrophonePermission) }
            )
        }

    }

}

/**
 *  udp host and port
 */
@Composable
private fun UdpSettings(
    editData: WakeWordUdpConfigurationData,
    isMicrophonePermissionRequestVisible: Boolean,
    onEvent: (WakeWordConfigurationUiEvent) -> Unit
) {

    Column {

        //udp host
        TextFieldListItem(
            label = MR.strings.host.stable,
            modifier = Modifier.testTag(TestTag.AudioRecordingUdpHost),
            value = editData.outputHost,
            onValueChange = { onEvent(UpdateUdpOutputHost(it)) },
            isLastItem = false
        )

        //udp port
        TextFieldListItem(
            label = MR.strings.port.stable,
            modifier = Modifier.testTag(TestTag.AudioRecordingUdpPort),
            value = editData.outputPortText,
            onValueChange = { onEvent(UpdateUdpOutputPort(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        //button to enabled microphone
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isMicrophonePermissionRequestVisible
        ) {
            FilledTonalButtonListItem(
                text = MR.strings.allowMicrophonePermission.stable,
                onClick = { onEvent(RequestMicrophonePermission) }
            )
        }

    }

}