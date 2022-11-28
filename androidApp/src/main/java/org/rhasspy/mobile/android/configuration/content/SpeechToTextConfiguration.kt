package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.ContentPaddingLevel1
import org.rhasspy.mobile.android.utils.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.viewModels.configuration.SpeechToTextConfigurationViewModel

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Preview
@Composable
fun SpeechToTextConfigurationContent(viewModel: SpeechToTextConfigurationViewModel = viewModel()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.SpeechToTextConfiguration),
        title = MR.strings.speechToText,
        viewModel = viewModel,
        testContent = { TestContent(viewModel) }
    ) {

        //drop down of option
        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.SpeechToTextOptions),
            selected = viewModel.speechToTextOption.collectAsState().value,
            onSelect = viewModel::selectSpeechToTextOption,
            values = viewModel.speechToTextOptions
        ) {

            if (viewModel.isSpeechToTextHttpSettingsVisible(it)) {
                SpeechToTextHTTP(viewModel)
            }
        }

    }

}

@Composable
private fun SpeechToTextHTTP(viewModel: SpeechToTextConfigurationViewModel) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //switch to use custom
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
            text = MR.strings.useCustomEndpoint,
            isChecked = viewModel.isUseCustomSpeechToTextHttpEndpoint.collectAsState().value,
            onCheckedChange = viewModel::toggleUseCustomHttpEndpoint
        )

        //input to edit http endpoint
        TextFieldListItem(
            enabled = viewModel.isSpeechToTextHttpEndpointChangeEnabled.collectAsState().value,
            modifier = Modifier.testTag(TestTag.Endpoint),
            value = viewModel.speechToTextHttpEndpoint.collectAsState().value,
            onValueChange = viewModel::updateSpeechToTextHttpEndpoint,
            label = MR.strings.speechToTextURL
        )

    }

}

@Composable
private fun TestContent(
    viewModel: SpeechToTextConfigurationViewModel
) {
    Column {
        //button to record text
        //record can be stopped
    }
}