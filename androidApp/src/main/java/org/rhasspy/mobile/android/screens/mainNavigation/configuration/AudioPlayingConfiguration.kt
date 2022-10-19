package org.rhasspy.mobile.android.screens.mainNavigation.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.android.utils.PageContent
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.viewModels.configuration.AudioPlayingConfigurationViewModel


/**
 * Content to configure audio playing
 * Drop Down of state
 * HTTP Endpoint
 */
@Preview
@Composable
fun AudioPlayingConfigurationContent(viewModel: AudioPlayingConfigurationViewModel = viewModel()) {

    PageContent(MR.strings.audioPlaying) {

        val audioPlayingOption by viewModel.audioPlayingOption.collectAsState()

        //drop down of available values
        DropDownEnumListItem(
            selected = audioPlayingOption,
            onSelect = viewModel::selectAudioPlayingOption,
            values = viewModel.audioPlayingOptionsList
        )

        //visibility of endpoint option
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewModel.isAudioPlayingEndpointVisible.collectAsState().value
        ) {

            //endpoint input field
            TextFieldListItem(
                value = viewModel.audioPlayingEndpoint.collectAsState().value,
                onValueChange = viewModel::changeAudioPlayingEndpoint,
                label = MR.strings.audioOutputURL
            )

        }
    }

}
