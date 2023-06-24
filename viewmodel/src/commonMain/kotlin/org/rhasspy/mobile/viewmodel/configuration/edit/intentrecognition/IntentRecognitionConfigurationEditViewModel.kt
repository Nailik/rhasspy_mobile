package org.rhasspy.mobile.viewmodel.configuration.edit.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.intentrecognition.IntentRecognitionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.intentrecognition.IntentRecognitionConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.edit.intentrecognition.IntentRecognitionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.edit.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.intentrecognition.IntentRecognitionConfigurationViewState.IntentRecognitionConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.IntentRecognitionConfigurationScreenDestination.EditScreen

@Stable
class IntentRecognitionConfigurationEditViewModel(
    service: IntentRecognitionService,
    private val viewStateCreator: ConfigurationEditViewStateCreator
) : IConfigurationEditViewModel(
    service = service
) {

    private val initialConfigurationData = IntentRecognitionConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(IntentRecognitionConfigurationViewState(initialConfigurationData))
    val viewState = combineState(_viewState, _editData) { viewState, editData ->
        viewState.copy(editData = editData)
    }

    override fun initViewStateCreator(
        configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    ): StateFlow<ConfigurationEditViewState> {
        return viewStateCreator(
            init = ::IntentRecognitionConfigurationData,
            editData = _editData,
            configurationEditViewState = configurationEditViewState
        )
    }


    val screen = navigator.topScreen(EditScreen)

    fun onEvent(event: IntentRecognitionConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _editData.update {
            when (change) {
                is ChangeIntentRecognitionHttpEndpoint -> it.copy(intentRecognitionHttpEndpoint = change.endpoint)
                is SelectIntentRecognitionOption -> it.copy(intentRecognitionOption = change.option)
                is SetUseCustomHttpEndpoint -> it.copy(isUseCustomIntentRecognitionHttpEndpoint = change.enabled)
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
            ConfigurationSetting.intentRecognitionOption.value = intentRecognitionOption
            ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value = isUseCustomIntentRecognitionHttpEndpoint
            ConfigurationSetting.intentRecognitionHttpEndpoint.value = intentRecognitionHttpEndpoint
        }
    }

}