package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.logic.services.httpclient.HttpClientPath
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiAction
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiAction.ChangeIntentRecognitionHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiAction.SelectIntentRecognitionOption
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiAction.ToggleUseCustomHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewState

/**
 * configuration content for intent recognition
 * drop down to select option
 * text field for endpoint
 */
@Preview
@Composable
fun IntentRecognitionConfigurationContent(viewModel: IntentRecognitionConfigurationViewModel = get()) {

    val viewState by viewModel.viewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.IntentRecognitionConfiguration),
        title = MR.strings.intentRecognition.stable,
        viewState = viewState,
        onAction = viewModel::onAction,
        onConsumed = viewModel::onConsumed,
        testContent = { TestContent(viewModel) }
    ) { contentViewState ->

        item {
            IntentRecognitionOptionContent(
                viewState = contentViewState,
                onAction = viewModel::onAction
            )
        }
    }
}

@Composable
private fun IntentRecognitionOptionContent(
    viewState: IntentRecognitionConfigurationViewState,
    onAction: (IntentRecognitionConfigurationUiAction) -> Unit
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.IntentRecognitionOptions),
        selected = viewState.intentRecognitionOption,
        onSelect = { onAction(SelectIntentRecognitionOption(it)) },
        values = viewState.intentRecognitionOptionList
    ) {option ->

        when(option) {
            IntentRecognitionOption.RemoteHTTP ->
                IntentRecognitionHTTP(
                    isUseCustomIntentRecognitionHttpEndpoint = viewState.isUseCustomIntentRecognitionHttpEndpoint,
                    intentRecognitionHttpEndpointText = viewState.intentRecognitionHttpEndpointText,
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
private fun IntentRecognitionHTTP(
    isUseCustomIntentRecognitionHttpEndpoint: Boolean,
    intentRecognitionHttpEndpointText: String,
    onAction: (IntentRecognitionConfigurationUiAction) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint.stable,
            isChecked = isUseCustomIntentRecognitionHttpEndpoint,
            onCheckedChange = { onAction(ToggleUseCustomHttpEndpoint) }
        )

        //http endpoint input field
        TextFieldListItem(
            enabled = isUseCustomIntentRecognitionHttpEndpoint,
            modifier = Modifier
                .testTag(TestTag.Endpoint)
                .padding(bottom = 8.dp),
            value = intentRecognitionHttpEndpointText,
            onValueChange = { onAction(ChangeIntentRecognitionHttpEndpoint(it)) },
            label = translate(MR.strings.rhasspyTextToIntentURL.stable, HttpClientPath.TextToIntent.path)
        )
    }

}

/**
 * text input and intent recognition execute button
 */
@Composable
private fun TestContent(viewModel: IntentRecognitionConfigurationViewModel) {

    Column {
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.TextToSpeechText),
            value = viewModel.testIntentRecognitionText.collectAsState().value,
            onValueChange = viewModel::updateTestIntentRecognitionText,
            label = MR.strings.textIntentRecognitionText.stable
        )

        FilledTonalButtonListItem(
            text = MR.strings.executeIntentRecognition.stable,
            onClick = viewModel::runIntentRecognition
        )
    }

}