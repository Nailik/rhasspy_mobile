package org.rhasspy.mobile.ui.configuration.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.*
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenConfig
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenItemContent
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
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.OpenPicoVoiceConsole
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.UpdateWakeWordPorcupineAccessToken
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationViewState.PorcupineViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationViewState.UdpViewState
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination.WakeWordConfigurationScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.WakeWordConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.WakeWordConfigurationScreenDestination.*

/**
 * Nav Host of Wake word configuration screens
 */
@Composable
fun WakeWordConfigurationContent() {

    val viewModel: WakeWordConfigurationEditViewModel = LocalViewModelFactory.current.getViewModel()

    val viewState by viewModel.viewState.collectAsState()

    Screen(kViewModel = viewModel) {
        val screen by viewModel.screen.collectAsState()
        val porcupineScreen by viewModel.porcupineScreen.collectAsState()

        val contentViewState by viewState.editViewState.collectAsState()
        val snackBarHostState = LocalSnackBarHostState.current
        val snackBarText = contentViewState.snackBarText?.let { translate(it) }

        LaunchedEffect(snackBarText) {
            snackBarText?.also {
                snackBarHostState.showSnackbar(message = it)
                viewModel.onEvent(ShowSnackBar)
            }
        }

        when (screen) {
            EditScreen -> WakeWordConfigurationOverview(screen, viewModel)
            EditPorcupineLanguageScreen -> PorcupineLanguageScreen(
                viewState = contentViewState.wakeWordPorcupineViewState,
                onEvent = viewModel::onEvent
            )

            EditPorcupineWakeWordScreen -> PorcupineKeywordScreen(
                porcupineScreen = porcupineScreen,
                viewState = contentViewState.wakeWordPorcupineViewState,
                onEvent = viewModel::onEvent
            )

            TestScreen -> WakeWordConfigurationOverview(screen, viewModel)
        }
    }

}

/**
 * Overview to configure wake word
 * Drop Down of option
 * porcupine wake word settings
 */
@Composable
private fun WakeWordConfigurationOverview(
    screen: WakeWordConfigurationScreenDestination,
    viewModel: WakeWordConfigurationEditViewModel
) {

    val viewState by viewModel.viewState.collectAsState()

    val contentViewState by viewState.editViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(WakeWordConfigurationScreen),
        screenType = screen.destinationType,
        config = ConfigurationScreenConfig(MR.strings.wakeWord.stable),
        viewState = viewState,
        onAction = { viewModel.onAction(it) },
        testContent = { TestContent(viewModel::onEvent) }
    ) {

        item {
            WakeWordConfigurationOptionContent(
                wakeWordOption = contentViewState.wakeWordOption,
                isMicrophonePermissionRequestVisible = contentViewState.isMicrophonePermissionRequestVisible,
                wakeWordOptions = contentViewState.wakeWordOptions,
                wakeWordPorcupineViewState = contentViewState.wakeWordPorcupineViewState,
                wakeWordUdpViewState = contentViewState.wakeWordUdpViewState,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
private fun WakeWordConfigurationOptionContent(
    wakeWordOption: WakeWordOption,
    isMicrophonePermissionRequestVisible: Boolean,
    wakeWordOptions: ImmutableList<WakeWordOption>,
    wakeWordPorcupineViewState: PorcupineViewState,
    wakeWordUdpViewState: UdpViewState,
    onEvent: (WakeWordConfigurationUiEvent) -> Unit
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.WakeWordOptions),
        selected = wakeWordOption,
        onSelect = { onEvent(SelectWakeWordOption(it)) },
        values = wakeWordOptions
    ) { option ->

        when (option) {
            WakeWordOption.Porcupine -> PorcupineConfiguration(
                viewState = wakeWordPorcupineViewState,
                isMicrophonePermissionRequestVisible = isMicrophonePermissionRequestVisible,
                onEvent = onEvent
            )

            WakeWordOption.Udp -> UdpSettings(
                viewState = wakeWordUdpViewState,
                isMicrophonePermissionRequestVisible = isMicrophonePermissionRequestVisible,
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
    viewState: PorcupineViewState,
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
            value = viewState.accessToken,
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
                val porcupineLanguageText by remember { derivedStateOf { viewState.porcupineLanguage.text } }
                Text(porcupineLanguageText)
            }
        )

        //wake word list
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineKeyword)
                .clickable { onEvent(Navigate(EditPorcupineWakeWordScreen)) },
            text = { Text(MR.strings.wakeWord.stable) },
            secondaryText = { Text("${viewState.keywordCount} ${translate(MR.strings.active.stable)}") }
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
    viewState: UdpViewState,
    isMicrophonePermissionRequestVisible: Boolean,
    onEvent: (WakeWordConfigurationUiEvent) -> Unit
) {

    Column {

        //udp host
        TextFieldListItem(
            label = MR.strings.host.stable,
            modifier = Modifier.testTag(TestTag.AudioRecordingUdpHost),
            value = viewState.outputHost,
            onValueChange = { onEvent(UpdateUdpOutputHost(it)) },
            isLastItem = false
        )

        //udp port
        TextFieldListItem(
            label = MR.strings.port.stable,
            modifier = Modifier.testTag(TestTag.AudioRecordingUdpPort),
            value = viewState.outputPortText,
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

/**
 * test button to start wake word detection test
 */
@Composable
private fun TestContent(onEvent: (WakeWordConfigurationUiEvent) -> Unit) {
    FilledTonalButtonListItem(
        text = MR.strings.startRecordAudio.stable,
        onClick = { onEvent(TestStartWakeWord) }
    )
}