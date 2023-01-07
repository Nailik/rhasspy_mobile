package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import org.rhasspy.mobile.services.httpclient.HttpClientPath
import org.rhasspy.mobile.viewmodel.configuration.TextToSpeechConfigurationViewModel


/**
 * Content to configure text to speech
 * Drop Down of state
 * HTTP Endpoint
 */
@Preview
@Composable
fun TextToSpeechConfigurationContent(viewModel: TextToSpeechConfigurationViewModel = get()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.TextToSpeechConfiguration),
        title = MR.strings.textToSpeech,
        viewModel = viewModel,
        testContent = { TestContent(viewModel) }
    ) {

        item {
            //drop down to select text to speech
            RadioButtonsEnumSelection(
                modifier = Modifier.testTag(TestTag.TextToSpeechOptions),
                selected = viewModel.textToSpeechOption.collectAsState().value,
                onSelect = viewModel::selectTextToSpeechOption,
                values = viewModel.textToSpeechOptions
            ) {

                if (viewModel.isTextToSpeechHttpSettingsVisible(it)) {
                    TextToSpeechHTTP(viewModel)
                }

            }
        }

    }

}

/**
 * http endpoint settings
 */
@Composable
private fun TextToSpeechHTTP(viewModel: TextToSpeechConfigurationViewModel) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint,
            isChecked = viewModel.isUseCustomTextToSpeechHttpEndpoint.collectAsState().value,
            onCheckedChange = viewModel::toggleUseCustomHttpEndpoint
        )

        //http endpoint input field
        TextFieldListItem(
            enabled = viewModel.isTextToSpeechHttpEndpointChangeEnabled.collectAsState().value,
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = viewModel.textToSpeechHttpEndpoint.collectAsState().value,
            onValueChange = viewModel::updateTextToSpeechHttpEndpoint,
            label = translate(MR.strings.rhasspyTextToSpeechURL, HttpClientPath.TextToSpeech.path)
        )

    }

}

/**
 * input field and execute button
 */
@Composable
private fun TestContent(
    viewModel: TextToSpeechConfigurationViewModel
) {
    Column {
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.TextToSpeechText),
            value = viewModel.testTextToSpeechText.collectAsState().value,
            onValueChange = viewModel::updateTestTextToSpeechText,
            label = MR.strings.textToSpeechText
        )

        FilledTonalButtonListItem(
            text = MR.strings.executeTextToSpeechText,
            onClick = viewModel::startTextToSpeech
        )
    }
}