package org.rhasspy.mobile.ui.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenConfig
import org.rhasspy.mobile.ui.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.main.LocalViewModelFactory
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewState
import org.rhasspy.mobile.viewmodel.navigation.Screen.ConfigurationScreen.ConfigurationDetailScreen.DialogManagementConfigurationScreen

/**
 * DropDown to select dialog management option
 */
@Composable
fun DialogManagementConfigurationContent(screen: DialogManagementConfigurationScreen) {
    val viewModel: DialogManagementConfigurationViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()
    val contentViewState by viewState.editViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenType = screen.type,
        config = ConfigurationScreenConfig(MR.strings.dialogManagement.stable),
        viewState = viewState,
        onAction = { viewModel.onAction(it) }
    ) {

        item {
            DialogManagementOptionContent(
                viewState = contentViewState,
                onAction = viewModel::onEvent
            )
        }

    }

}

@Composable
private fun DialogManagementOptionContent(
    viewState: DialogManagementConfigurationViewState,
    onAction: (DialogManagementConfigurationUiEvent) -> Unit
) {
    //drop down to select option
    RadioButtonsEnumSelection(
        modifier = Modifier.testTag(TestTag.DialogManagementOptions),
        selected = viewState.dialogManagementOption,
        onSelect = { onAction(SelectDialogManagementOption(it)) },
        values = viewState.dialogManagementOptionList
    ) { option ->

        when (option) {
            DialogManagementOption.Local ->
                LocalDialogManagementSettings(
                    textAsrTimeoutText = viewState.textAsrTimeoutText,
                    intentRecognitionTimeoutText = viewState.intentRecognitionTimeoutText,
                    recordingTimeoutText = viewState.recordingTimeoutText,
                    onAction = onAction
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
    onAction: (DialogManagementConfigurationUiEvent) -> Unit
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