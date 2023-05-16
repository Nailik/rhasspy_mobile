package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.AudioPlayingConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.DialogManagementConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.DialogManagementConfigurationScreenDestination.EditScreen

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
@Stable
class DialogManagementConfigurationViewModel(
    service: DialogManagerService,
    navigator: Navigator
) : IConfigurationViewModel<DialogManagementConfigurationViewState>(
    service = service,
    initialViewState = ::DialogManagementConfigurationViewState,
    navigator = navigator
) {

    val screen = navigator.getBackStack(DialogManagementConfigurationScreenDestination::class, EditScreen)

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
        when(action) {
            BackClick -> navigator.popBackStack()
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