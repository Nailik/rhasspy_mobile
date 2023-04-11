package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreenType
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.ContentPaddingLevel1
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiAction
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiAction.ChangeIntentRecognitionTimeout
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiAction.ChangeRecordingTimeout
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiAction.ChangeTextAsrTimeout
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiAction.SelectDialogManagementOption
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewState

/**
 * DropDown to select dialog management option
 */
@Preview
@Composable
fun DialogManagementConfigurationContent(viewModel: DialogManagementConfigurationViewModel = get()) {

    val viewState by viewModel.viewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.DialogManagementConfiguration),
        title = MR.strings.dialogManagement.stable,
        viewState = viewState,
        onAction = viewModel::onAction
    ) { contentViewState ->

        item {
            DialogManagementOptionContent(
                viewState = contentViewState,
                onAction = viewModel::onAction
            )
        }

    }

}

@Composable
private fun DialogManagementOptionContent(
    viewState: DialogManagementConfigurationViewState,
    onAction: (DialogManagementConfigurationUiAction) -> Unit
) {
    //drop down to select option
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.DialogManagementOptions),
        selected = viewState.dialogManagementOption,
        onSelect = { onAction(SelectDialogManagementOption(it)) },
        values = viewState.dialogManagementOptionList
    ) {
        if (it == DialogManagementOption.Local) {
            //http endpoint
            LocalDialogManagementSettings(
                textAsrTimeoutText = viewState.textAsrTimeoutText,
                intentRecognitionTimeoutText = viewState.intentRecognitionTimeoutText,
                recordingTimeoutText = viewState.recordingTimeoutText,
                onAction = onAction
            )
        }
    }
}

/**
 * http configuration for intent handling
 * field to set endpoint
 */
@Composable
private fun LocalDialogManagementSettings(
    textAsrTimeoutText: String,
    intentRecognitionTimeoutText: String,
    recordingTimeoutText: String,
    onAction: (DialogManagementConfigurationUiAction) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //asr timeout
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.TextAsrTimeout),
            value = textAsrTimeoutText,
            onValueChange = { onAction(ChangeTextAsrTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = MR.strings.textAsrTimeoutText.stable
        )

        //intent recognition timeout
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.IntentRecognitionTimeout),
            value = intentRecognitionTimeoutText,
            onValueChange = { onAction(ChangeIntentRecognitionTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = MR.strings.intentRecognitionTimeoutText.stable
        )

        //recording timeout
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.RecordingTimeout),
            value = recordingTimeoutText,
            onValueChange = { onAction(ChangeRecordingTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = MR.strings.recordingTimeoutText.stable
        )

    }

}