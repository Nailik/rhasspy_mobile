package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.configuration.test.EventListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.ContentPaddingLevel1
import org.rhasspy.mobile.android.utils.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.viewModels.configuration.TextToSpeechConfigurationViewModel


/**
 * Content to configure text to speech
 * Drop Down of state
 * HTTP Endpoint
 */
@Preview
@Composable
fun TextToSpeechConfigurationContent(viewModel: TextToSpeechConfigurationViewModel = viewModel()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.TextToSpeechConfiguration),
        title = MR.strings.textToSpeech,
        viewModel = viewModel,
        testContent = { modifier -> TestContent(modifier, viewModel) }
    ) {

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
            modifier = Modifier
                .testTag(TestTag.Endpoint),
            value = viewModel.textToSpeechHttpEndpoint.collectAsState().value,
            onValueChange = viewModel::updateTextToSpeechHttpEndpoint,
            label = MR.strings.rhasspyTextToSpeechURL
        )

    }

}

@Composable
private fun TestContent(
    modifier: Modifier,
    viewModel: TextToSpeechConfigurationViewModel
) {

    val eventsList by viewModel.events.collectAsState()

    LazyColumn(modifier = modifier.fillMaxHeight()) {
        items(eventsList) { item ->
            EventListItem(item)
        }
    }
}