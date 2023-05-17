package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Action.RunIntentRecognition
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.IntentRecognitionConfigurationScreenDestination.EditScreen

@Stable
class IntentRecognitionConfigurationViewModel(
    service: IntentRecognitionService
) : IConfigurationViewModel<IntentRecognitionConfigurationViewState>(
    service = service,
    initialViewState = ::IntentRecognitionConfigurationViewState
) {

    val screen = navigator.topScreen(EditScreen)

    fun onEvent(event: IntentRecognitionConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        updateViewState {
            when (change) {
                is ChangeIntentRecognitionHttpEndpoint -> it.copy(intentRecognitionHttpEndpoint = change.endpoint)
                is SelectIntentRecognitionOption -> it.copy(intentRecognitionOption = change.option)
                is SetUseCustomHttpEndpoint -> it.copy(isUseCustomIntentRecognitionHttpEndpoint = change.enabled)
                is UpdateTestIntentRecognitionText -> it.copy(testIntentRecognitionText = change.text)
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            RunIntentRecognition -> runIntentRecognition()
            BackClick -> navigator.popBackStack()
        }
    }

    override fun onDiscard() {}

    override fun onSave() {
        ConfigurationSetting.intentRecognitionOption.value = data.intentRecognitionOption
        ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value = data.isUseCustomIntentRecognitionHttpEndpoint
        ConfigurationSetting.intentRecognitionHttpEndpoint.value = data.intentRecognitionHttpEndpoint
    }

    private fun runIntentRecognition() {
        testScope.launch {
            //await for mqtt
            if (get<IntentRecognitionServiceParams>().intentRecognitionOption == IntentRecognitionOption.RemoteMQTT) {
                get<MqttService>()
                    .isHasStarted
                    .map { it }
                    .distinctUntilChanged()
                    .first { it }
            }

            get<IntentRecognitionService>().recognizeIntent("", data.testIntentRecognitionText)
        }
    }

}