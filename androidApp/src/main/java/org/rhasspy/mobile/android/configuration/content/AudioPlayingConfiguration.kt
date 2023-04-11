package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.ImmutableList
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreenType
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.android.content.elements.translate
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.ContentPaddingLevel1
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.logic.services.httpclient.HttpClientPath
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.ChangeAudioPlayingHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.ChangeAudioPlayingMqttSiteId
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.SelectAudioOutputOption
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.SelectAudioPlayingOption
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.ToggleUseCustomHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewState

/**
 * Content to configure audio playing
 * Drop Down of state
 * HTTP Endpoint
 */
@Preview(showBackground = true)
@Composable
fun AudioPlayingConfigurationContent(viewModel: AudioPlayingConfigurationViewModel = get()) {

    val viewState by viewModel.viewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.AudioPlayingConfiguration),
        title = MR.strings.audioPlaying.stable,
        viewState = viewState,
        onAction = viewModel::onAction,
        testContent = { TestContent(viewModel) }
    ) { contentViewState ->

        item {
            AudioPlayingOptionContent(
                viewState = contentViewState,
                onAction = viewModel::onAction
            )
        }
    }

}

@Composable
private fun AudioPlayingOptionContent(
    viewState: AudioPlayingConfigurationViewState,
    onAction: (AudioPlayingConfigurationUiAction) -> Unit
) {

    //radio buttons list of available values
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.AudioPlayingOptions),
        selected = viewState.audioPlayingOption,
        onSelect = { onAction(SelectAudioPlayingOption(it)) },
        values = viewState.audioPlayingOptionList
    ) {

        when (it) {
            AudioPlayingOption.Local -> LocalConfigurationContent(
                audioOutputOption = viewState.audioOutputOption,
                audioOutputOptionList = viewState.audioOutputOptionList,
                onAction = onAction
            )

            AudioPlayingOption.RemoteHTTP -> HttpEndpointConfigurationContent(
                isUseCustomAudioPlayingHttpEndpoint = viewState.isUseCustomAudioPlayingHttpEndpoint,
                audioPlayingHttpEndpoint = viewState.audioPlayingHttpEndpoint,
                onAction = onAction
            )
            AudioPlayingOption.RemoteMQTT -> MqttSiteIdConfigurationContent(
            audioPlayingMqttSiteId = viewState.audioPlayingMqttSiteId,
            onAction = onAction
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
    onAction: (AudioPlayingConfigurationUiAction) -> Unit
) {

    //visibility of local output options
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        RadioButtonsEnumSelectionList(
            modifier = Modifier.testTag(TestTag.AudioOutputOptions),
            selected = audioOutputOption,
            onSelect = { onAction(SelectAudioOutputOption(it)) },
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
    onAction: (AudioPlayingConfigurationUiAction) -> Unit
) {

    //visibility of endpoint option
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint.stable,
            isChecked = isUseCustomAudioPlayingHttpEndpoint,
            onCheckedChange = { onAction(ToggleUseCustomHttpEndpoint) }
        )

        //http endpoint input field
        TextFieldListItem(
            enabled = isUseCustomAudioPlayingHttpEndpoint,
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = audioPlayingHttpEndpoint,
            onValueChange = { onAction(ChangeAudioPlayingHttpEndpoint(it)) },
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
    onAction: (AudioPlayingConfigurationUiAction) -> Unit
) {

    //visibility of endpoint option
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //http endpoint input field
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.ConfigurationSiteId),
            value = audioPlayingMqttSiteId,
            onValueChange = { onAction(ChangeAudioPlayingMqttSiteId(it)) },
            label = translate(MR.strings.siteId.stable)
        )

    }

}

/**
 * test content, play button
 */
@Composable
private fun TestContent(viewModel: AudioPlayingConfigurationViewModel) {

    FilledTonalButtonListItem(
        text = MR.strings.executePlayTestAudio.stable,
        onClick = viewModel::playTestAudio
    )

}