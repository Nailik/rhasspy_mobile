package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.toLongOrNullOrConstant
import org.rhasspy.mobile.data.service.ServiceState.Success
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.*

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
@Stable
class DialogManagementConfigurationViewModel(
    private val mapper: DialogManagementConfigurationDataMapper,
) : ConfigurationViewModel(
    serviceState = MutableStateFlow(Success),
) {

    private val initialData get() = mapper(ConfigurationSetting.pipelineData.value)
    private val _viewState = MutableStateFlow(DialogManagementConfigurationViewState(initialData))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::initialData,
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
        _viewState.update { it.copy(editData = initialData) }
    }

    override fun onSave() {
        ConfigurationSetting.pipelineData.value = mapper(_viewState.value.editData)
        _viewState.update { it.copy(editData = initialData) }
    }

}