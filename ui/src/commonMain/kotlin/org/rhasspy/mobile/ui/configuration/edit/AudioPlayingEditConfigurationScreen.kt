package org.rhasspy.mobile.ui.configuration.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.httpclient.HttpClientPath
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenItemEdit
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewState.AudioPlayingConfigurationData

/**
 * Content to configure audio playing
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun AudioPlayingEditConfigurationScreen() {

    val viewModel: AudioPlayingConfigurationViewModel = LocalViewModelFactory.current.getViewModel()

    val configurationEditViewState by viewModel.configurationEditViewState.collectAsState()

    ConfigurationScreenItemEdit(
        modifier = Modifier,
        kViewModel = viewModel,
        title = MR.strings.audioPlaying.stable,
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {

        val viewState by viewModel.viewState.collectAsState()

        AudioPlayingEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun AudioPlayingEditContent(
    editData: AudioPlayingConfigurationData,
    onEvent: (AudioPlayingConfigurationUiEvent) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            AudioPlayingOptionContent(
                editData = editData,
                onEvent = onEvent
            )
        }

    }

}

@Composable
private fun AudioPlayingOptionContent(
    editData: AudioPlayingConfigurationData,
    onEvent: (AudioPlayingConfigurationUiEvent) -> Unit
) {

    //radio buttons list of available values
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.AudioPlayingOptions),
        selected = editData.audioPlayingOption,
        onSelect = { onEvent(SelectEditAudioPlayingOption(it)) },
        values = editData.audioPlayingOptionList
    ) { option ->

        when (option) {
            AudioPlayingOption.Local -> LocalConfigurationContent(
                audioOutputOption = editData.audioOutputOption,
                audioOutputOptionList = editData.audioOutputOptionList,
                onEvent = onEvent
            )

            AudioPlayingOption.RemoteHTTP -> HttpEndpointConfigurationContent(
                isUseCustomAudioPlayingHttpEndpoint = editData.isUseCustomAudioPlayingHttpEndpoint,
                audioPlayingHttpEndpoint = editData.audioPlayingHttpEndpoint,
                onEvent = onEvent
            )

            AudioPlayingOption.RemoteMQTT -> MqttSiteIdConfigurationContent(
                audioPlayingMqttSiteId = editData.audioPlayingMqttSiteId,
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
            onValueChange = { onEvent(ChangeEditAudioPlayingHttpEndpoint(it)) },
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
            onValueChange = { onEvent(ChangeEditAudioPlayingMqttSiteId(it)) },
            label = translate(MR.strings.siteId.stable)
        )

    }

}