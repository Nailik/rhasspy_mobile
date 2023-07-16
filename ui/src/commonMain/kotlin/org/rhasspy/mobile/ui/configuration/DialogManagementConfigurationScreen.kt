package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewState.DialogManagementConfigurationData

/**
 * DropDown to select dialog management option
 */
@Composable
fun DialogManagementConfigurationScreen() {

    val viewModel: DialogManagementConfigurationViewModel = LocalViewModelFactory.current.getViewModel()

    val configurationEditViewState by viewModel.configurationViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenViewModel = viewModel,
        title = MR.strings.dialogManagement.stable,
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {

        val viewState by viewModel.viewState.collectAsState()

        DialogManagementEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun DialogManagementEditContent(
    editData: DialogManagementConfigurationData,
    onEvent: (DialogManagementConfigurationUiEvent) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            DialogManagementOptionContent(
                editData = editData,
                onEvent = onEvent
            )
        }

    }

}

@Composable
private fun DialogManagementOptionContent(
    editData: DialogManagementConfigurationData,
    onEvent: (DialogManagementConfigurationUiEvent) -> Unit
) {

    //drop down to select option
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.DialogManagementOptions),
        selected = editData.dialogManagementOption,
        onSelect = { onEvent(SelectDialogManagementOption(it)) },
        values = editData.dialogManagementOptionList
    ) { option ->

        when (option) {
            DialogManagementOption.Local ->
                LocalDialogManagementSettings(
                    textAsrTimeoutText = editData.textAsrTimeoutText,
                    intentRecognitionTimeoutText = editData.intentRecognitionTimeoutText,
                    recordingTimeoutText = editData.recordingTimeoutText,
                    onEvent = onEvent
                )

            else -> {}
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
    onEvent: (DialogManagementConfigurationUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        //asr timeout
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.TextAsrTimeout),
            value = textAsrTimeoutText,
            onValueChange = { onEvent(ChangeTextAsrTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = MR.strings.textAsrTimeoutText.stable
        )

        //intent recognition timeout
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.IntentRecognitionTimeout),
            value = intentRecognitionTimeoutText,
            onValueChange = { onEvent(ChangeIntentRecognitionTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = MR.strings.intentRecognitionTimeoutText.stable
        )

        //recording timeout
        TextFieldListItem(
            modifier = Modifier.testTag(TestTag.RecordingTimeout),
            value = recordingTimeoutText,
            onValueChange = { onEvent(ChangeRecordingTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = MR.strings.recordingTimeoutText.stable
        )

    }

}