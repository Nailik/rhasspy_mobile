package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.DialogManagementConfigurationScreenDestination.EditScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.DialogManagementConfigurationScreenDestination.TestScreen

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
@Stable
class DialogManagementConfigurationViewModel(
    service: DialogManagerService
) : IConfigurationViewModel<DialogManagementConfigurationViewState>(
    service = service,
    initialViewState = ::DialogManagementConfigurationViewState,
    testPageDestination = TestScreen
) {

    val screen = navigator.topScreen(EditScreen)

    fun onEvent(event: DialogManagementConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    fun onChange(change: Change) {
        updateViewState {
            when (change) {
                is ChangeIntentRecognitionTimeout -> it.copy(intentRecognitionTimeoutText = change.timeout)
                is ChangeRecordingTimeout -> it.copy(recordingTimeoutText = change.timeout)
                is ChangeTextAsrTimeout -> it.copy(textAsrTimeoutText = change.timeout)
                is SelectDialogManagementOption -> it.copy(dialogManagementOption = change.option)
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
        }
    }

    override fun onDiscard() {}

    override fun onSave() {
        ConfigurationSetting.dialogManagementOption.value = data.dialogManagementOption
        ConfigurationSetting.textAsrTimeout.value = data.textAsrTimeout
        ConfigurationSetting.intentRecognitionTimeout.value = data.intentRecognitionTimeout
        ConfigurationSetting.recordingTimeout.value = data.recordingTimeout
    }

}