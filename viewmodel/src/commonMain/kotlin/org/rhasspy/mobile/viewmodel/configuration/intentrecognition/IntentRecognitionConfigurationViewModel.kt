package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.services.intentrecognition.IIntentRecognitionService
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationViewState.IntentRecognitionConfigurationData

@Stable
class IntentRecognitionConfigurationViewModel(
    service: IIntentRecognitionService
) : ConfigurationViewModel(
    service = service
) {

    private val _viewState = MutableStateFlow(IntentRecognitionConfigurationViewState(IntentRecognitionConfigurationData()))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::IntentRecognitionConfigurationData,
            viewState = viewState,
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
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is ChangeIntentRecognitionHttpEndpoint -> copy(intentRecognitionHttpEndpoint = change.endpoint)
                    is SelectIntentRecognitionOption -> copy(intentRecognitionOption = change.option)
                    is SetUseCustomHttpEndpoint -> copy(isUseCustomIntentRecognitionHttpEndpoint = change.enabled)
                }
            })
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
        }
    }

    override fun onDiscard() {
        _viewState.update { it.copy(editData = IntentRecognitionConfigurationData()) }
    }

    override fun onSave() {
        with(_viewState.value.editData) {
            ConfigurationSetting.intentRecognitionOption.value = intentRecognitionOption
            ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value = isUseCustomIntentRecognitionHttpEndpoint
            ConfigurationSetting.intentRecognitionHttpEndpoint.value = intentRecognitionHttpEndpoint
        }
    }

}