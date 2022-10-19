package org.rhasspy.mobile.android.screens.mainNavigation.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.android.utils.PageContent
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

    PageContent(MR.strings.textToSpeech) {

        //drop down to select text to speech
        DropDownEnumListItem(
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
