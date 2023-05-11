package org.rhasspy.mobile.android.configuration.content

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.configuration.ConfigurationScreenConfig
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreenType
import org.rhasspy.mobile.android.configuration.content.porcupine.PorcupineKeywordScreen
import org.rhasspy.mobile.android.configuration.content.porcupine.PorcupineLanguageScreen
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItemVisibility
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.main.LocalSnackbarHostState
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.android.permissions.RequiresMicrophonePermission
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.MicrophonePermissionAllowed
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.TestStartWakeWord
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.OpenPicoVoiceConsole
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.UpdateWakeWordPorcupineAccessToken
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.PorcupineViewState
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.UdpViewState

enum class WakeWordConfigurationScreens(val route: String) {
    Overview("WakeWordConfigurationScreens_Overview"),
    PorcupineKeyword("WakeWordConfigurationScreens_PorcupineKeyword"),
    PorcupineLanguage("WakeWordConfigurationScreens_PorcupineLanguage")
}

/**
 * Nav Host of Wake word configuration screens
 */
@Preview
@Composable
fun WakeWordConfigurationContent() {
    val viewModel: WakeWordConfigurationViewModel = LocalViewModelFactory.current.getViewModel()
    val navController = rememberNavController()

    val viewState by viewModel.viewState.collectAsState()
    val contentViewState by viewState.editViewState.collectAsState()
    val snackBarHostState = LocalSnackbarHostState.current
    val snackBarText = contentViewState.snackBarText?.let { translate(it) }

    LaunchedEffect(snackBarText) {
        snackBarText?.also {
            snackBarHostState.showSnackbar(message = it)
            viewModel.onEvent(ShowSnackBar)
        }
    }

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = WakeWordConfigurationScreens.Overview.route
        ) {

            composable(WakeWordConfigurationScreens.Overview.route) {
                WakeWordConfigurationOverview(viewModel)
            }

            composable(WakeWordConfigurationScreens.PorcupineLanguage.route) {
                PorcupineLanguageScreen(
                    viewState = contentViewState.wakeWordPorcupineViewState,
                    onEvent = viewModel::onEvent
                )
            }

            composable(WakeWordConfigurationScreens.PorcupineKeyword.route) {
                PorcupineKeywordScreen(
                    viewState = contentViewState.wakeWordPorcupineViewState,
                    onEvent = viewModel::onEvent
                )
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

    val viewState by viewModel.viewState.collectAsState()
    val contentViewState by viewState.editViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.WakeWordConfiguration),
        config = ConfigurationScreenConfig(MR.strings.wakeWord.stable),
        viewState = viewState,
        onAction = { viewModel.onAction(it) },
        onConsumed = { viewModel.onConsumed(it) },
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

        //opens page for porcupine keyword or language selection
        val navigation = LocalNavController.current

        //opens page for porcupine language selection
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineLanguage)
                .clickable { navigation.navigate(WakeWordConfigurationScreens.PorcupineLanguage.route) },
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
                .clickable { navigation.navigate(WakeWordConfigurationScreens.PorcupineKeyword.route) },
            text = { Text(MR.strings.wakeWord.stable) },
            secondaryText = { Text("${viewState.keywordCount} ${translate(MR.strings.active.stable)}") }
        )

        //button to enabled microphone
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isMicrophonePermissionRequestVisible
        ) {
            RequiresMicrophonePermission(
                informationText = MR.strings.microphonePermissionInfoRecord.stable,
                onClick = { onEvent(MicrophonePermissionAllowed) }
            ) { onClick ->
                FilledTonalButtonListItem(
                    text = MR.strings.allowMicrophonePermission.stable,
                    onClick = onClick
                )
            }
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
            RequiresMicrophonePermission(
                informationText = MR.strings.microphonePermissionInfoRecord.stable,
                onClick = { onEvent(MicrophonePermissionAllowed) }
            ) { onClick ->
                FilledTonalButtonListItem(
                    text = MR.strings.allowMicrophonePermission.stable,
                    onClick = onClick
                )
            }
        }

    }

}

/**
 * test button to start wake word detection test
 */
@Composable
private fun TestContent(onEvent: (WakeWordConfigurationUiEvent) -> Unit) {

    RequiresMicrophonePermission(
        informationText = MR.strings.microphonePermissionInfoRecord.stable,
        onClick = { onEvent(TestStartWakeWord) }
    ) { onClick ->
        FilledTonalButtonListItem(
            text = MR.strings.startRecordAudio.stable,
            onClick = onClick
        )
    }

}