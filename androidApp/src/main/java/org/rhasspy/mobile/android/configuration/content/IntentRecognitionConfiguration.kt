package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import org.rhasspy.mobile.viewmodel.configuration.IntentRecognitionConfigurationViewModel

/**
 * configuration content for intent recognition
 * drop down to select option
 * text field for endpoint
 */
@Preview
@Composable
fun IntentRecognitionConfigurationContent(viewModel: IntentRecognitionConfigurationViewModel = get()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.IntentRecognitionConfiguration),
        title = MR.strings.intentRecognition,
        viewModel = viewModel,
        testContent = { TestContent(viewModel) }
    ) {

        item {
            //drop down to select intent recognition option
            RadioButtonsEnumSelection(
                modifier = Modifier.testTag(TestTag.IntentRecognitionOptions),
                selected = viewModel.intentRecognitionOption.collectAsState().value,
                onSelect = viewModel::selectIntentRecognitionOption,
                values = viewModel.intentRecognitionOptionList
            ) {

                if (viewModel.isIntentRecognitionHttpSettingsVisible(it)) {
                    IntentRecognitionHTTP(viewModel)
                }

            }
        }
    }
}

/**
 * http endpoint settings
 */
@Composable
private fun IntentRecognitionHTTP(viewModel: IntentRecognitionConfigurationViewModel) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint,
            isChecked = viewModel.isUseCustomIntentRecognitionHttpEndpoint.collectAsState().value,
            onCheckedChange = viewModel::toggleUseCustomHttpEndpoint
        )

        //http endpoint input field
        TextFieldListItem(
            enabled = viewModel.isIntentRecognitionHttpEndpointChangeEnabled.collectAsState().value,
            modifier = Modifier
                .testTag(TestTag.Endpoint)
                .padding(bottom = 8.dp),
            value = viewModel.intentRecognitionHttpEndpoint.collectAsState().value,
            onValueChange = viewModel::changeIntentRecognitionHttpEndpoint,
            label = translate(MR.strings.rhasspyTextToIntentURL, HttpClientPath.TextToIntent.path)
        )
    }

}

/**
 * text input and intent recognition execute button
 */
@Composable
private fun TestContent(
    viewModel: IntentRecognitionConfigurationViewModel
) {
    Column {
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.TextToSpeechText),
            value = viewModel.testIntentRecognitionText.collectAsState().value,
            onValueChange = viewModel::updateTestIntentRecognitionText,
            label = MR.strings.textIntentRecognitionText
        )

        FilledTonalButtonListItem(
            text = MR.strings.executeIntentRecognition,
            onClick = viewModel::runIntentRecognition
        )
    }
}