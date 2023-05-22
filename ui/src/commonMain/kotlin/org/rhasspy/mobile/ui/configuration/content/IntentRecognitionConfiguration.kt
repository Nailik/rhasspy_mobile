package org.rhasspy.mobile.ui.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.httpclient.HttpClientPath
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenConfig
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Action.RunIntentRecognition
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewState
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination.IntentRecognitionConfigurationScreen

/**
 * configuration content for intent recognition
 * drop down to select option
 * text field for endpoint
 */
@Composable
fun IntentRecognitionConfigurationContent() {
    val viewModel: IntentRecognitionConfigurationViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()
    val screen by viewModel.screen.collectAsState()
    val contentViewState by viewState.editViewState.collectAsState()

    Screen(viewModel) {
        ConfigurationScreenItemContent(
            modifier = Modifier.testTag(IntentRecognitionConfigurationScreen),
            screenType = screen.destinationType,
            config = ConfigurationScreenConfig(MR.strings.intentRecognition.stable),
            viewState = viewState,
            onAction = viewModel::onAction,
            testContent = {
                TestContent(
                    testIntentRecognitionText = contentViewState.testIntentRecognitionText,
                    onEvent = viewModel::onEvent
                )
            }
        ) {

            item {
                IntentRecognitionOptionContent(
                    viewState = contentViewState,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }

}

@Composable
private fun IntentRecognitionOptionContent(
    viewState: IntentRecognitionConfigurationViewState,
    onEvent: (IntentRecognitionConfigurationUiEvent) -> Unit
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.IntentRecognitionOptions),
        selected = viewState.intentRecognitionOption,
        onSelect = { onEvent(SelectIntentRecognitionOption(it)) },
        values = viewState.intentRecognitionOptionList
    ) { option ->

        when (option) {
            IntentRecognitionOption.RemoteHTTP ->
                IntentRecognitionHTTP(
                    isUseCustomIntentRecognitionHttpEndpoint = viewState.isUseCustomIntentRecognitionHttpEndpoint,
                    intentRecognitionHttpEndpointText = viewState.intentRecognitionHttpEndpointText,
                    onEvent = onEvent
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
    onEvent: (IntentRecognitionConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint.stable,
            isChecked = isUseCustomIntentRecognitionHttpEndpoint,
            onCheckedChange = { onEvent(SetUseCustomHttpEndpoint(it)) }
        )

        //http endpoint input field
        TextFieldListItem(
            enabled = isUseCustomIntentRecognitionHttpEndpoint,
            modifier = Modifier
                .testTag(TestTag.Endpoint)
                .padding(bottom = 8.dp),
            value = intentRecognitionHttpEndpointText,
            onValueChange = { onEvent(ChangeIntentRecognitionHttpEndpoint(it)) },
            label = translate(MR.strings.rhasspyTextToIntentURL.stable, HttpClientPath.TextToIntent.path)
        )
    }

}

/**
 * text input and intent recognition execute button
 */
@Composable
private fun TestContent(
    testIntentRecognitionText: String,
    onEvent: (IntentRecognitionConfigurationUiEvent) -> Unit
) {

    Column {
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.TextToSpeechText),
            value = testIntentRecognitionText,
            onValueChange = { onEvent(UpdateTestIntentRecognitionText(it)) },
            label = MR.strings.textIntentRecognitionText.stable
        )

        FilledTonalButtonListItem(
            text = MR.strings.executeIntentRecognition.stable,
            onClick = { onEvent(RunIntentRecognition) },
        )
    }

}