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
import org.rhasspy.mobile.data.IntentRecognitionOptions
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * List element for intent recognition configuration
 * shows which option is selected
 */
@Composable
fun IntentRecognitionConfigurationItem(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.intentRecognition,
        secondaryText = viewModel.intentRecognitionOption.flow.collectAsState().value.text,
        screen = BottomSheetScreens.IntentRecognition
    )

}

/**
 * configuration content for intent recognition
 * drop down to select option
 * text field for endpoint
 */
@Composable
fun IntentRecognitionConfigurationContent(viewModel: ConfigurationScreenViewModel) {

    val intentRecognitionOption by viewModel.intentRecognitionOption.flow.collectAsState()

    ConfigurationListContent(MR.strings.intentRecognition) {

        DropDownEnumListItem(
            selected = intentRecognitionOption,
            onSelect = viewModel.intentRecognitionOption::set,
            values = IntentRecognitionOptions::values
        )

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = intentRecognitionOption == IntentRecognitionOptions.RemoteHTTP
        ) {

            TextFieldListItem(
                value = viewModel.intentRecognitionEndpoint.flow.collectAsState().value,
                onValueChange = viewModel.intentRecognitionEndpoint::set,
                label = MR.strings.rhasspyTextToIntentURL
            )

        }

    }
}
