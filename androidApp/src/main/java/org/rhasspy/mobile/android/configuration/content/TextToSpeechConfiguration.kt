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
import org.rhasspy.mobile.android.content.elements.translate
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.ContentPaddingLevel1
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.services.httpclient.HttpClientPath
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiAction
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiAction.Change.SelectTextToSpeechOption
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiAction.Change.ToggleUseCustomHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiAction.Change.UpdateTextToSpeechHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewState

/**
 * Content to configure text to speech
 * Drop Down of state
 * HTTP Endpoint
 */
@Preview
@Composable
fun TextToSpeechConfigurationContent(viewModel: TextToSpeechConfigurationViewModel = get()) {

    val viewState by viewModel.viewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.TextToSpeechConfiguration),
        title = MR.strings.textToSpeech.stable,
        viewState = viewState,
        onAction = viewModel::onAction,
        onConsumed = viewModel::onConsumed,
        testContent = { TestContent(viewModel) }
    ) { contentViewState ->

        item {
            TextToSpeechOptionContent(
                viewState = contentViewState,
                onAction = viewModel::onAction
            )
        }

    }

}

@Composable
private fun TextToSpeechOptionContent(
    viewState: TextToSpeechConfigurationViewState,
    onAction: (TextToSpeechConfigurationUiAction) -> Unit
) {
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.TextToSpeechOptions),
        selected = viewState.textToSpeechOption,
        onSelect = { onAction(SelectTextToSpeechOption(it)) },
        values = viewState.textToSpeechOptions
    ) {

        when (it) {
            TextToSpeechOption.RemoteHTTP ->  TextToSpeechHTTP(
                isUseCustomTextToSpeechHttpEndpoint = viewState.isUseCustomTextToSpeechHttpEndpoint,
                textToSpeechHttpEndpoint = viewState.textToSpeechHttpEndpoint,
                onAction = onAction
            )
            else -> {}
        }

    }
}

/**
 * http endpoint settings
 */
@Composable
private fun TextToSpeechHTTP(
    isUseCustomTextToSpeechHttpEndpoint: Boolean,
    textToSpeechHttpEndpoint: String,
    onAction: (TextToSpeechConfigurationUiAction) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint.stable,
            isChecked = isUseCustomTextToSpeechHttpEndpoint,
            onCheckedChange = {  onAction(ToggleUseCustomHttpEndpoint) }
        )

        //http endpoint input field
        TextFieldListItem(
            enabled = isUseCustomTextToSpeechHttpEndpoint,
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = textToSpeechHttpEndpoint,
            onValueChange = {  onAction(UpdateTextToSpeechHttpEndpoint(it)) },
            label = translate(MR.strings.rhasspyTextToSpeechURL.stable, HttpClientPath.TextToSpeech.path)
        )

    }

}

/**
 * input field and execute button
 */
@Composable
private fun TestContent(viewModel: TextToSpeechConfigurationViewModel) {

    Column {
        TextFieldListItem(
            label = MR.strings.textToSpeechText.stable,
            modifier = Modifier.testTag(TestTag.TextToSpeechText),
            value = viewModel.testTextToSpeechText.collectAsState().value,
            onValueChange = viewModel::updateTestTextToSpeechText
        )

        FilledTonalButtonListItem(
            text = MR.strings.executeTextToSpeechText.stable,
            onClick = viewModel::startTextToSpeech
        )
    }

}