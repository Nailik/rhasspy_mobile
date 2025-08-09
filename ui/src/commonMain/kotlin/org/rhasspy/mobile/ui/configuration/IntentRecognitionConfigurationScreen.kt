package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.ChangeIntentRecognitionHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.SelectIntentRecognitionOption
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.SetUseCustomHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewState.IntentRecognitionConfigurationData

/**
 * configuration content for intent recognition
 * drop down to select option
 * text field for endpoint
 */
@Composable
fun IntentRecognitionConfigurationScreen() {

    val viewModel: IntentRecognitionConfigurationViewModel =
        LocalViewModelFactory.current.getViewModel()

    val configurationEditViewState by viewModel.configurationViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenViewModel = viewModel,
        title = MR.strings.intentRecognition.stable,
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {

        val viewState by viewModel.viewState.collectAsState()

        IntentRecognitionEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
fun IntentRecognitionEditContent(
    editData: IntentRecognitionConfigurationData,
    onEvent: (IntentRecognitionConfigurationUiEvent) -> Unit,
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            IntentRecognitionOptionContent(
                editData = editData,
                onEvent = onEvent
            )
        }

    }

}

@Composable
private fun IntentRecognitionOptionContent(
    editData: IntentRecognitionConfigurationData,
    onEvent: (IntentRecognitionConfigurationUiEvent) -> Unit,
) {

    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.IntentRecognitionOptions),
        selected = editData.intentRecognitionOption,
        onSelect = { onEvent(SelectIntentRecognitionOption(it)) },
        values = editData.intentRecognitionOptionList
    ) { option ->

        when (option) {
            IntentRecognitionOption.RemoteHTTP ->
                IntentRecognitionHTTP(
                    isUseCustomIntentRecognitionHttpEndpoint = editData.isUseCustomIntentRecognitionHttpEndpoint,
                    intentRecognitionHttpEndpointText = editData.intentRecognitionHttpEndpointText,
                    onEvent = onEvent
                )

            else -> Unit
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
    onEvent: (IntentRecognitionConfigurationUiEvent) -> Unit,
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
            label = translate(
                MR.strings.rhasspyTextToIntentURL.stable,
                HttpClientPath.TextToIntent.path
            )
        )
    }

}