package org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent.Action.RunIntentHandlingTest
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationViewState.IntentHandlingConfigurationData

@Stable
class IntentHandlingConfigurationEditViewModel(
    service: DialogManagerService,
    private val viewStateCreator: ConfigurationEditViewStateCreator
) : IConfigurationEditViewModel(
    service = service
) {

    private val initialConfigurationData = IntentHandlingConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(IntentHandlingConfigurationViewState(initialConfigurationData))
    val viewState = combineState(_viewState, _editData) { viewState, editData ->
        viewState.copy(editData = editData)
    }

    override fun initViewStateCreator(
        configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    ): StateFlow<ConfigurationEditViewState> {
        return viewStateCreator(
            init = ::IntentHandlingConfigurationData,
            editData = _editData,
            configurationEditViewState = configurationEditViewState
        )
    }

    fun onEvent(event: IntentHandlingConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _editData.update {
            when (change) {
                is ChangeIntentHandlingHomeAssistantAccessToken -> it.copy(intentHandlingHomeAssistantAccessToken = change.token)
                is ChangeIntentHandlingHomeAssistantEndpoint -> it.copy(intentHandlingHomeAssistantEndpoint = change.endpoint)
                is ChangeIntentHandlingHttpEndpoint -> it.copy(intentHandlingHttpEndpoint = change.endpoint)
                is SelectIntentHandlingHomeAssistantOption -> it.copy(intentHandlingHomeAssistantOption = change.option)
                is SelectIntentHandlingOption -> it.copy(intentHandlingOption = change.option)
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
            ConfigurationSetting.intentHandlingOption.value = intentHandlingOption
            ConfigurationSetting.intentHandlingHttpEndpoint.value = intentHandlingHttpEndpoint
            ConfigurationSetting.intentHandlingHomeAssistantEndpoint.value = intentHandlingHomeAssistantEndpoint
            ConfigurationSetting.intentHandlingHomeAssistantAccessToken.value = intentHandlingHomeAssistantAccessToken
            ConfigurationSetting.intentHandlingHomeAssistantOption.value = intentHandlingHomeAssistantOption
        }
    }

}