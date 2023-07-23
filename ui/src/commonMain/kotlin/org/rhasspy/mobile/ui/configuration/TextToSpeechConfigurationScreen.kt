package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.httpclient.HttpClientPath
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewState.TextToSpeechConfigurationData

/**
 * Content to configure text to speech
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun TextToSpeechConfigurationScreen() {

    val viewModel: TextToSpeechConfigurationViewModel = LocalViewModelFactory.current.getViewModel()

    val configurationEditViewState by viewModel.configurationViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenViewModel = viewModel,
        title = MR.strings.textToSpeechText.stable,
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {

        val viewState by viewModel.viewState.collectAsState()

        TextToSpeechEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun TextToSpeechEditContent(
    editData: TextToSpeechConfigurationData,
    onEvent: (TextToSpeechConfigurationUiEvent) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            TextToSpeechOptionContent(
                editData = editData,
                onEvent = onEvent
            )
        }

    }

}


@Composable
private fun TextToSpeechOptionContent(
    editData: TextToSpeechConfigurationData,
    onEvent: (TextToSpeechConfigurationUiEvent) -> Unit
) {
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.TextToSpeechOptions),
        selected = editData.textToSpeechOption,
        onSelect = { onEvent(SelectTextToSpeechOption(it)) },
        values = editData.textToSpeechOptions
    ) {

        when (it) {
            TextToSpeechOption.RemoteHTTP -> TextToSpeechHTTP(
                isUseCustomTextToSpeechHttpEndpoint = editData.isUseCustomTextToSpeechHttpEndpoint,
                textToSpeechHttpEndpointText = editData.textToSpeechHttpEndpointText,
                onAction = onEvent
            )

            else -> Unit
        }

    }
}

/**
 * http endpoint settings
 */
@Composable
private fun TextToSpeechHTTP(
    isUseCustomTextToSpeechHttpEndpoint: Boolean,
    textToSpeechHttpEndpointText: String,
    onAction: (TextToSpeechConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint.stable,
            isChecked = isUseCustomTextToSpeechHttpEndpoint,
            onCheckedChange = { onAction(SetUseCustomHttpEndpoint(it)) }
        )

        //http endpoint input field
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.Endpoint),
            enabled = isUseCustomTextToSpeechHttpEndpoint,
            value = textToSpeechHttpEndpointText,
            onValueChange = { onAction(UpdateTextToSpeechHttpEndpoint(it)) },
            label = translate(MR.strings.rhasspyTextToSpeechURL.stable, HttpClientPath.TextToSpeech.path)
        )

    }

}