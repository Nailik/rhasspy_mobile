package org.rhasspy.mobile.android.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.screens.ConfigurationScreens
import org.rhasspy.mobile.android.utils.ConfigurationListContent
import org.rhasspy.mobile.android.utils.ConfigurationListItem
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.data.TextToSpeechOptions
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * List element for text to speech configuration
 * shows which option is selected
 */
@Composable
fun TextToSpeechConfigurationItem(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.textToSpeech,
        secondaryText = viewModel.textToSpeechOption.flow.collectAsState().value.text,
        screen = ConfigurationScreens.TextToSpeech
    )

}


/**
 * Content to configure text to speech
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun TextToSpeechConfigurationContent(viewModel: ConfigurationScreenViewModel) {

    val textToSpeechOption by viewModel.textToSpeechOption.flow.collectAsState()

    ConfigurationListContent(MR.strings.textToSpeech) {

        DropDownEnumListItem(
            selected = textToSpeechOption,
            onSelect = viewModel.textToSpeechOption::set,
            values = TextToSpeechOptions::values
        )

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = textToSpeechOption == TextToSpeechOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = viewModel.textToSpeechEndpoint.flow.collectAsState().value,
                onValueChange = viewModel.textToSpeechEndpoint::set,
                label = MR.strings.rhasspyTextToSpeechURL
            )

        }

    }

}
