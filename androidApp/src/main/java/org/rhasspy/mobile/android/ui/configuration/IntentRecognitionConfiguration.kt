package org.rhasspy.mobile.android.ui.configuration

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
import org.rhasspy.mobile.viewModels.configuration.IntentRecognitionConfigurationViewModel

/**
 * configuration content for intent recognition
 * drop down to select option
 * text field for endpoint
 */
@Preview
@Composable
fun IntentRecognitionConfigurationContent(viewModel: IntentRecognitionConfigurationViewModel = viewModel()) {

    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        //drop down to select intent recognition option
        DropDownEnumListItem(
            selected = viewModel.intentRecognitionOption.collectAsState().value,
            onSelect = viewModel::selectIntentRecognitionOption,
            values = viewModel.intentRecognitionOptionsList
        )

        //visibility of endpoint input
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewModel.isRemoteHttpEndpointVisible.collectAsState().value
        ) {

            //http endpoint input field
            TextFieldListItem(
                value = viewModel.intentRecognitionEndpoint.collectAsState().value,
                onValueChange = viewModel::changeIntentRecognitionHttpEndpoint,
                label = MR.strings.rhasspyTextToIntentURL
            )

        }

    }
}
