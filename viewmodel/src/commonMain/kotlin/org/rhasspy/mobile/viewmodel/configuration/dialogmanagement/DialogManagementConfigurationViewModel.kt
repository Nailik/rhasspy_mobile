package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.toLongOrNullOrConstant
import org.rhasspy.mobile.data.data.toLongOrZero
import org.rhasspy.mobile.logic.domains.dialog.IDialogManagerService
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewState.DialogManagementConfigurationData

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
@Stable
class DialogManagementConfigurationViewModel(
    service: IDialogManagerService,
) : ConfigurationViewModel(
    serviceState = service.serviceState
) {

    private val _viewState = MutableStateFlow(DialogManagementConfigurationViewState(DialogManagementConfigurationData()))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::DialogManagementConfigurationData,
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

    fun onEvent(event: DialogManagementConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is ChangeIntentRecognitionTimeout -> copy(intentRecognitionTimeout = change.timeout.toLongOrNullOrConstant())
                    is ChangeRecordingTimeout         -> copy(recordingTimeout = change.timeout.toLongOrNullOrConstant())
                    is ChangeTextAsrTimeout           -> copy(textAsrTimeout = change.timeout.toLongOrNullOrConstant())
                    is SelectDialogManagementOption   -> copy(dialogManagementOption = change.option)
                }
            })
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick          -> navigator.onBackPressed()
            is Action.Navigate -> navigator.navigate(action.destination)
        }
    }

    override fun onDiscard() {
        _viewState.update { it.copy(editData = DialogManagementConfigurationData()) }
    }

    override fun onSave() {
        with(_viewState.value.editData) {
            ConfigurationSetting.dialogManagementOption.value = dialogManagementOption
            ConfigurationSetting.textAsrTimeout.value = textAsrTimeout.toLongOrZero()
            ConfigurationSetting.intentRecognitionTimeout.value = intentRecognitionTimeout.toLongOrZero()
            ConfigurationSetting.recordingTimeout.value = recordingTimeout.toLongOrZero()
        }
    }

}