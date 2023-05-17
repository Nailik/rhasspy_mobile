package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.android.configuration.ConfigurationScreenConfig
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.logic.services.httpclient.HttpClientPath
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Action.PlayTestAudio
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewState
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination.AudioPlayingConfigurationScreen

/**
 * Content to configure audio playing
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun AudioPlayingConfigurationContent() {
    val viewModel: AudioPlayingConfigurationViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(viewModel) {
        val viewState by viewModel.viewState.collectAsState()
        val screen by viewModel.screen.collectAsState()
        val contentViewState by viewState.editViewState.collectAsState()

        ConfigurationScreenItemContent(
            modifier = Modifier.testTag(AudioPlayingConfigurationScreen),
            screenType = screen.destinationType,
            config = ConfigurationScreenConfig(MR.strings.audioPlaying.stable),
            viewState = viewState,
            onAction = viewModel::onAction,
            testContent = { TestContent(viewModel::onEvent) }
        ) {

            item {
                AudioPlayingOptionContent(
                    viewState = contentViewState,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }

}

@Composable
private fun AudioPlayingOptionContent(
    viewState: AudioPlayingConfigurationViewState,
    onEvent: (AudioPlayingConfigurationUiEvent) -> Unit
) {

    //radio buttons list of available values
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.AudioPlayingOptions),
        selected = viewState.audioPlayingOption,
        onSelect = { onEvent(SelectAudioPlayingOption(it)) },
        values = viewState.audioPlayingOptionList
    ) { option ->

        when (option) {
            AudioPlayingOption.Local -> LocalConfigurationContent(
                audioOutputOption = viewState.audioOutputOption,
                audioOutputOptionList = viewState.audioOutputOptionList,
                onEvent = onEvent
            )

            AudioPlayingOption.RemoteHTTP -> HttpEndpointConfigurationContent(
                isUseCustomAudioPlayingHttpEndpoint = viewState.isUseCustomAudioPlayingHttpEndpoint,
                audioPlayingHttpEndpoint = viewState.audioPlayingHttpEndpoint,
                onEvent = onEvent
            )

            AudioPlayingOption.RemoteMQTT -> MqttSiteIdConfigurationContent(
                audioPlayingMqttSiteId = viewState.audioPlayingMqttSiteId,
                onEvent = onEvent
            )

            else -> {}
        }

    }

}

/**
 * show output options for local audio
 */
@Composable
private fun LocalConfigurationContent(
    audioOutputOption: AudioOutputOption,
    audioOutputOptionList: ImmutableList<AudioOutputOption>,
    onEvent: (AudioPlayingConfigurationUiEvent) -> Unit
) {

    //visibility of local output options
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        RadioButtonsEnumSelectionList(
            modifier = Modifier.testTag(TestTag.AudioOutputOptions),
            selected = audioOutputOption,
            onSelect = { onEvent(SelectAudioOutputOption(it)) },
            values = audioOutputOptionList
        )

    }

}

/**
 * show http endpoint options
 */
@Composable
private fun HttpEndpointConfigurationContent(
    isUseCustomAudioPlayingHttpEndpoint: Boolean,
    audioPlayingHttpEndpoint: String,
    onEvent: (AudioPlayingConfigurationUiEvent) -> Unit
) {

    //visibility of endpoint option
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint.stable,
            isChecked = isUseCustomAudioPlayingHttpEndpoint,
            onCheckedChange = { onEvent(SetUseCustomHttpEndpoint(it)) }
        )

        //http endpoint input field
        TextFieldListItem(
            enabled = isUseCustomAudioPlayingHttpEndpoint,
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = audioPlayingHttpEndpoint,
            onValueChange = { onEvent(ChangeAudioPlayingHttpEndpoint(it)) },
            label = translate(MR.strings.audioOutputURL.stable, HttpClientPath.PlayWav.path)
        )

    }

}


/**
 * show mqtt site id options
 */
@Composable
private fun MqttSiteIdConfigurationContent(
    audioPlayingMqttSiteId: String,
    onEvent: (AudioPlayingConfigurationUiEvent) -> Unit
) {

    //visibility of endpoint option
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //http endpoint input field
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.ConfigurationSiteId),
            value = audioPlayingMqttSiteId,
            onValueChange = { onEvent(ChangeAudioPlayingMqttSiteId(it)) },
            label = translate(MR.strings.siteId.stable)
        )

    }

}

/**
 * test content, play button
 */
@Composable
private fun TestContent(onEvent: (AudioPlayingConfigurationUiEvent) -> Unit) {

    FilledTonalButtonListItem(
        text = MR.strings.executePlayTestAudio.stable,
        onClick = { onEvent(PlayTestAudio) }
    )

}