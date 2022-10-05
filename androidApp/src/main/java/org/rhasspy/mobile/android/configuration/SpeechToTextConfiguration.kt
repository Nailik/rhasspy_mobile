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
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel


/**
 * List element for speech to text configuration
 * shows which option is selected
 */
@Composable
fun SpeechToTextConfigurationItem(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.speechToText,
        secondaryText = viewModel.speechToTextOption.flow.collectAsState().value.text,
        screen = ConfigurationScreens.SpeechToText
    )
}

/**
 * Content to configure speech to text
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun SpeechToTextConfigurationContent(viewModel: ConfigurationScreenViewModel) {

    val speechToTextOption by viewModel.speechToTextOption.flow.collectAsState()

    ConfigurationListContent(MR.strings.speechToText) {

        DropDownEnumListItem(
            selected = speechToTextOption,
            onSelect = viewModel.speechToTextOption::set,
            values = SpeechToTextOptions::values
        )

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = speechToTextOption == SpeechToTextOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = viewModel.speechToTextHttpEndpoint.flow.collectAsState().value,
                onValueChange = viewModel.speechToTextHttpEndpoint::set,
                label = MR.strings.speechToTextURL
            )

        }

    }

}
