package org.rhasspy.mobile.android.screens.mainNavigation.configuration

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
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
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

    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        //drop down of option
        DropDownEnumListItem(
            selected = viewModel.speechToTextOption.collectAsState().value,
            onSelect = viewModel::selectSpeechToTextOption,
            values = viewModel.speechToTextOptions
        )

        //visibility of http endpoint
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewModel.speechToTextHttpEndpointVisible.collectAsState().value
        ) {

            //input to edit http endpoint
            TextFieldListItem(
                value = viewModel.speechToTextHttpEndpoint.collectAsState().value,
                onValueChange = viewModel::updateSpeechToTextHttpEndpoint,
                label = MR.strings.speechToTextURL
            )

        }

    }

}
