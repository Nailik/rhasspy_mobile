package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.toLongOrNullOrConstant
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
@Stable
class DialogManagementConfigurationViewModel(
    private val mapper: DialogManagementConfigurationDataMapper,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(DialogManagementConfigurationViewState(mapper(ConfigurationSetting.pipelineData.value)))
    val viewState = _viewState.readOnly

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
                    is ChangeTextAsrTimeout           -> copy(textAsrTimeout = change.timeout.toLongOrNullOrConstant())
                    is SelectDialogManagementOption   -> copy(dialogManagementOption = change.option)
                }
            })
        }
        ConfigurationSetting.pipelineData.value = mapper(_viewState.value.editData)
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick   -> navigator.onBackPressed()
            is Navigate -> navigator.navigate(action.destination)
        }
    }

}