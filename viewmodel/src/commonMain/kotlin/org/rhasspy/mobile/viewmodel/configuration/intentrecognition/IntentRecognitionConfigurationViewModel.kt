package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewState.IntentRecognitionConfigurationData

@Stable
class IntentRecognitionConfigurationViewModel(
    service: IntentRecognitionService
) : IConfigurationViewModel(
    service = service
) {

    private val initialConfigurationData = IntentRecognitionConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(IntentRecognitionConfigurationViewState(initialConfigurationData))
    val viewState = combineState(_viewState, _editData) { viewState, editData ->
        viewState.copy(editData = editData)
    }

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<IConfigurationViewState>
    ): StateFlow<IConfigurationViewState> {
        return viewStateCreator(
            init = ::IntentRecognitionConfigurationData,
            editData = _editData,
            configurationViewState = configurationViewState
        )
    }

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