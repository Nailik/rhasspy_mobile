package org.rhasspy.mobile.viewmodel.configuration.edit.dialogmanagement

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.toLongOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.dialogmanagement.DialogManagementConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.dialogmanagement.DialogManagementConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.edit.dialogmanagement.DialogManagementConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.edit.dialogmanagement.DialogManagementConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.dialogmanagement.DialogManagementConfigurationViewState.DialogManagementConfigurationData

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
@Stable
class DialogManagementConfigurationEditViewModel(
    service: DialogManagerService,
    private val viewStateCreator: ConfigurationEditViewStateCreator
) : IConfigurationEditViewModel(
    service = service
) {

    private val initialConfigurationData = DialogManagementConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(DialogManagementConfigurationViewState(initialConfigurationData))
    val viewState = combineState(_viewState, _editData) { viewState, editData ->
        viewState.copy(editData = editData)
    }

    override fun initViewStateCreator(
        configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    ): StateFlow<ConfigurationEditViewState> {
        return viewStateCreator(
            init = ::DialogManagementConfigurationData,
            editData = _editData,
            configurationEditViewState = configurationEditViewState
        )
    }

    fun onEvent(event: DialogManagementConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _editData.update {
            when (change) {
                is ChangeIntentRecognitionTimeout -> it.copy(intentRecognitionTimeout = change.timeout.toLongOrNull())
                is ChangeRecordingTimeout -> it.copy(recordingTimeout = change.timeout.toLongOrNull())
                is ChangeTextAsrTimeout -> it.copy(textAsrTimeout = change.timeout.toLongOrNull())
                is SelectDialogManagementOption -> it.copy(dialogManagementOption = change.option)
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
        }
    }

    override fun onDiscard() {}

    override fun onSave() {
        with(_editData.value) {
            ConfigurationSetting.dialogManagementOption.value = dialogManagementOption
            ConfigurationSetting.textAsrTimeout.value = textAsrTimeout.toLongOrZero()
            ConfigurationSetting.intentRecognitionTimeout.value = intentRecognitionTimeout.toLongOrZero()
            ConfigurationSetting.recordingTimeout.value = recordingTimeout.toLongOrZero()
        }
    }

}