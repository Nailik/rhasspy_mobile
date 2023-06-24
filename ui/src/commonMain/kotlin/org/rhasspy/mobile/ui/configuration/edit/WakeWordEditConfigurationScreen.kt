package org.rhasspy.mobile.ui.configuration.edit

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
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenItemEdit
import org.rhasspy.mobile.ui.configuration.edit.porcupine.PorcupineKeywordScreen
import org.rhasspy.mobile.ui.configuration.edit.porcupine.PorcupineLanguageScreen
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItemVisibility
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.Action.RequestMicrophonePermission
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.OpenPicoVoiceConsole
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.UpdateWakeWordPorcupineAccessToken
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordPorcupineConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordUdpConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination.WakeWordConfigurationScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.WakeWordConfigurationScreenDestination.*

/**
 * Nav Host of Wake word configuration screens
 */
@Composable
fun WakeWordEditConfigurationScreen() {

    val viewModel: WakeWordConfigurationEditViewModel = LocalViewModelFactory.current.getViewModel()

    val configurationEditViewState by viewModel.configurationEditViewState.collectAsState()

    val viewState by viewModel.viewState.collectAsState()

    when (viewState.screen) {
        EditScreen ->
            WakeWordConfigurationEditContent(
                viewModel = viewModel,
                viewState = viewState,
                onEvent = viewModel::onEvent,
                configurationEditViewState = configurationEditViewState
            )

        EditPorcupineLanguageScreen ->
            PorcupineLanguageScreen(
                editData = viewState.editData.wakeWordPorcupineConfigurationData,
                onEvent = viewModel::onEvent
            )

        EditPorcupineWakeWordScreen ->
            PorcupineKeywordScreen(
                porcupineScreen = viewState.porcupineWakeWordScreen,
                editData = viewState.editData.wakeWordPorcupineConfigurationData,
                onEvent = viewModel::onEvent
            )
    }

}

/**
 * Overview to configure wake word
 * Drop Down of option
 * porcupine wake word settings
 */
@Composable
private fun WakeWordConfigurationEditContent(
    viewModel: WakeWordConfigurationEditViewModel,
    viewState: WakeWordConfigurationViewState,
    configurationEditViewState: ConfigurationEditViewState,
    onEvent: (WakeWordConfigurationUiEvent) -> Unit
) {

    ConfigurationScreenItemEdit(
        modifier = Modifier,
        kViewModel = viewModel,
        title = MR.strings.wakeWord.stable,
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {

            item {
                WakeWordConfigurationOptionContent(
                    viewState = viewState,
                    onEvent = onEvent
                )
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

            WakeWordOption.Udp ->
                UdpSettings(
                    editData = viewState.editData.wakeWordUdpConfigurationData,
                    isMicrophonePermissionRequestVisible = viewState.isMicrophonePermissionRequestVisible,
                    onEvent = onEvent
                )

            else -> {}
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