package org.rhasspy.mobile.android.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.screens.BottomSheetScreens
import org.rhasspy.mobile.android.utils.ConfigurationListContent
import org.rhasspy.mobile.android.utils.ConfigurationListItem
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.data.AudioPlayingOptions
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * List element for audio playing configuration
 * shows which option is selected
 */
@Composable
fun AudioPlayingConfigurationItem(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.audioPlaying,
        secondaryText = viewModel.audioPlayingOption.flow.collectAsState().value.text,
        screen = BottomSheetScreens.AudioPlaying
    )

}

/**
 * Content to configure audio playing
 * Drop Down of state
 * HTTP Endpoint
 */
@Composable
fun AudioPlayingConfigurationContent(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListContent(MR.strings.audioPlaying) {

        val audioPlayingOption by viewModel.audioPlayingOption.flow.collectAsState()

        DropDownEnumListItem(
            selected = audioPlayingOption,
            onSelect = viewModel.audioPlayingOption::set,
            values = AudioPlayingOptions::values
        )

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = audioPlayingOption == AudioPlayingOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = viewModel.audioPlayingEndpoint.flow.collectAsState().value,
                onValueChange = viewModel.audioPlayingEndpoint::set,
                label = MR.strings.audioOutputURL
            )

        }
    }

}
