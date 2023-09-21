package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.clickable
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
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewState.DialogManagementConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.SettingsScreenDestination.DeviceSettings

/**
 * DropDown to select dialog management option
 */
@Composable
fun DialogManagementConfigurationScreen(viewModel: DialogManagementConfigurationViewModel) {

    val configurationEditViewState by viewModel.configurationViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenViewModel = viewModel,
        title = MR.strings.dialog_pipeline.stable,
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
                DialogManagementSettingsLocal(
                    textAsrTimeoutText = editData.textAsrTimeoutText,
                    intentRecognitionTimeoutText = editData.intentRecognitionTimeoutText,
                    onEvent = onEvent
                )

            DialogManagementOption.Rhasspy2HermesMQTT ->
                DialogManagementSettingsMqtt(
                    textAsrTimeoutText = editData.textAsrTimeoutText,
                    intentRecognitionTimeoutText = editData.intentRecognitionTimeoutText,
                    onEvent = onEvent
                )

            else -> Unit
        }

    }

}

@Composable
private fun DialogManagementSettingsLocal(
    textAsrTimeoutText: String,
    intentRecognitionTimeoutText: String,
    onEvent: (DialogManagementConfigurationUiEvent) -> Unit
) {
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        DialogManagementSettings(
            textAsrTimeoutText = textAsrTimeoutText,
            intentRecognitionTimeoutText = intentRecognitionTimeoutText,
            onEvent = onEvent
        )

        //opens page for device settings
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineLanguage)
                .clickable { onEvent(Navigate(DeviceSettings)) },
            text = { Text(MR.strings.device.stable) },
            secondaryText = { Text(MR.strings.deviceSettingsLocalDialogInformation.stable) }
        )

    }
}

@Composable
private fun DialogManagementSettingsMqtt(
    textAsrTimeoutText: String,
    intentRecognitionTimeoutText: String,
    onEvent: (DialogManagementConfigurationUiEvent) -> Unit
) {
    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        DialogManagementSettings(
            textAsrTimeoutText = textAsrTimeoutText,
            intentRecognitionTimeoutText = intentRecognitionTimeoutText,
            onEvent = onEvent
        )

        //opens page for device settings
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineLanguage)
                .clickable { onEvent(Navigate(DeviceSettings)) },
            text = { Text(MR.strings.device.stable) },
            secondaryText = { Text(MR.strings.deviceSettingsMqttDialogInformation.stable) }
        )

    }
}

/**
 * http configuration for intent handling
 * field to set endpoint
 */
@Composable
private fun DialogManagementSettings(
    textAsrTimeoutText: String,
    intentRecognitionTimeoutText: String,
    onEvent: (DialogManagementConfigurationUiEvent) -> Unit
) {

    //asr timeout
    TextFieldListItem(
        modifier = Modifier.testTag(TestTag.TextAsrTimeout),
        value = textAsrTimeoutText,
        onValueChange = { onEvent(ChangeTextAsrTimeout(it)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        label = MR.strings.textAsrTimeoutText.stable,
        isLastItem = false
    )

    //intent recognition timeout
    TextFieldListItem(
        modifier = Modifier.testTag(TestTag.IntentRecognitionTimeout),
        value = intentRecognitionTimeoutText,
        onValueChange = { onEvent(ChangeIntentRecognitionTimeout(it)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        label = MR.strings.intentRecognitionTimeoutText.stable,
        isLastItem = false
    )

}