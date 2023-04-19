package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Action.RunIntentRecognition
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiEvent.Change.*

@Stable
class IntentRecognitionConfigurationViewModel(
    service: IntentRecognitionService,
) : IConfigurationViewModel<IntentRecognitionConfigurationViewState>(
    service = service,
    initialViewState = ::IntentRecognitionConfigurationViewState
) {

    fun onEvent(event: IntentRecognitionConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        contentViewState.update {
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
        }
    }

    override fun onSave() {
        ConfigurationSetting.intentRecognitionOption.value = data.intentRecognitionOption
        ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value = data.isUseCustomIntentRecognitionHttpEndpoint
        ConfigurationSetting.intentRecognitionHttpEndpoint.value = data.intentRecognitionHttpEndpoint
    }

    override fun initializeTestParams() {
        get<IntentRecognitionServiceParams> {
            parametersOf(
                IntentRecognitionServiceParams(
                    intentRecognitionOption = data.intentRecognitionOption
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomTextToSpeechHttpEndpoint = data.isUseCustomIntentRecognitionHttpEndpoint,
                    intentRecognitionHttpEndpoint = data.intentRecognitionHttpEndpoint,
                )
            )
        }
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