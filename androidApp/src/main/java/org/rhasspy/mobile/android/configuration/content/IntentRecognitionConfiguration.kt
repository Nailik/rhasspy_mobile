package org.rhasspy.mobile.android.configuration.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
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
import org.rhasspy.mobile.android.utils.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.utils.SwitchListItem
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

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.IntentRecognitionConfiguration),
        title = MR.strings.intentRecognition,
        hasUnsavedChanges = viewModel.hasUnsavedChanges,
        onSave = viewModel::save,
        onTest = viewModel::test,
        onDiscard = viewModel::discard
    ) {

        //drop down to select intent recognition option
        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.IntentRecognitionOptions),
            selected = viewModel.intentRecognitionOption.collectAsState().value,
            onSelect = viewModel::selectIntentRecognitionOption,
            values = viewModel.intentRecognitionOptionsList
        )

        //visibility of endpoint input
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewModel.isIntentRecognitionHttpSettingsVisible.collectAsState().value
        ) {

            Column {

                //switch to use custom
                SwitchListItem(
                    modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
                    text = MR.strings.useCustomEndpoint,
                    isChecked = viewModel.isUseCustomIntentRecognitionHttpEndpoint.collectAsState().value,
                    onCheckedChange = viewModel::toggleUseCustomHttpEndpoint
                )

                //http endpoint input field
                TextFieldListItem(
                    enabled = viewModel.isIntentRecognitionHttpEndpointChangeEnabled.collectAsState().value,
                    modifier = Modifier.testTag(TestTag.Endpoint),
                    value = viewModel.intentRecognitionHttpEndpoint.collectAsState().value,
                    onValueChange = viewModel::changeIntentRecognitionHttpEndpoint,
                    label = MR.strings.rhasspyTextToIntentURL
                )

            }
        }

    }
}
