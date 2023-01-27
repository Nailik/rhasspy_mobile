package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
import org.rhasspy.mobile.logic.services.httpclient.HttpClientPath
import org.rhasspy.mobile.viewmodel.configuration.AudioPlayingConfigurationViewModel

/**
 * Content to configure audio playing
 * Drop Down of state
 * HTTP Endpoint
 */
@Preview(showBackground = true)
@Composable
fun AudioPlayingConfigurationContent(viewModel: AudioPlayingConfigurationViewModel = get()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.AudioPlayingConfiguration),
        title = MR.strings.audioPlaying,
        viewModel = viewModel,
        testContent = { TestContent(viewModel) }
    ) {

        item {
            val audioPlayingOption by viewModel.audioPlayingOption.collectAsState()
            //radio buttons list of available values
            RadioButtonsEnumSelection(
                modifier = Modifier.testTag(TestTag.AudioPlayingOptions),
                selected = audioPlayingOption,
                onSelect = viewModel::selectAudioPlayingOption,
                values = viewModel.audioPlayingOptionList
            ) {

                if (viewModel.isAudioPlayingLocalSettingsVisible(it)) {
                    LocalConfigurationContent(viewModel)
                }

                if (viewModel.isAudioPlayingHttpEndpointSettingsVisible(it)) {
                    HttpEndpointConfigurationContent(viewModel)
                }

                if (viewModel.isAudioPlayingMqttSiteIdSettingsVisible(it)) {
                    MqttSiteIdConfigurationContent(viewModel)
                }

            }
        }
    }

}

/**
 * show output options for local audio
 */
@Composable
private fun LocalConfigurationContent(viewModel: AudioPlayingConfigurationViewModel) {

    //visibility of local output options
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        RadioButtonsEnumSelectionList(
            modifier = Modifier.testTag(TestTag.AudioOutputOptions),
            selected = viewModel.audioOutputOption.collectAsState().value,
            onSelect = viewModel::selectAudioOutputOption,
            values = viewModel.audioOutputOptionList
        )

    }

}

/**
 * show http endpoint options
 */
@Composable
private fun HttpEndpointConfigurationContent(viewModel: AudioPlayingConfigurationViewModel) {

    //visibility of endpoint option
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint,
            isChecked = viewModel.isUseCustomAudioPlayingHttpEndpoint.collectAsState().value,
            onCheckedChange = viewModel::toggleUseCustomHttpEndpoint
        )

        //http endpoint input field
        TextFieldListItem(
            enabled = viewModel.isAudioPlayingHttpEndpointChangeEnabled.collectAsState().value,
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = viewModel.audioPlayingHttpEndpoint.collectAsState().value,
            onValueChange = viewModel::changeAudioPlayingHttpEndpoint,
            label = translate(MR.strings.audioOutputURL, HttpClientPath.PlayWav.path)
        )

    }

}


/**
 * show mqtt site id options
 */
@Composable
private fun MqttSiteIdConfigurationContent(viewModel: AudioPlayingConfigurationViewModel) {

    //visibility of endpoint option
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //http endpoint input field
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.ConfigurationSiteId),
            value = viewModel.audioPlayingMqttSiteId.collectAsState().value,
            onValueChange = viewModel::changeAudioPlayingMqttSiteId,
            label = translate(MR.strings.siteId)
        )

    }

}

/**
 * test content, play button
 */
@Composable
private fun TestContent(viewModel: AudioPlayingConfigurationViewModel) {

    FilledTonalButtonListItem(
        text = MR.strings.executePlayTestAudio,
        onClick = viewModel::playTestAudio
    )

}