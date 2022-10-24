package org.rhasspy.mobile.android.configuration.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.RadioButtonsEnumSelection
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
        hasUnsavedChanges = MutableStateFlow(false),
        onSave = viewModel::save,
        onTest = viewModel::test,
        onDiscard = {  }
    ) {

        //drop down to select text to speech
        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.TextToSpeechOptions),
            selected = viewModel.textToSpeechOption.collectAsState().value,
            onSelect = viewModel::selectTextToSpeechOption,
            values = viewModel.textToSpeechOptions
        )

        //visibility of http endpoint settings
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewModel.textToSpeechHttpEndpointVisible.collectAsState().value
        ) {

            //http endpoint input field
            TextFieldListItem(
                value = viewModel.textToSpeechHttpEndpoint.collectAsState().value,
                onValueChange = viewModel::updateTextToSpeechHttpEndpoint,
                label = MR.strings.rhasspyTextToSpeechURL
            )

        }

    }

}
