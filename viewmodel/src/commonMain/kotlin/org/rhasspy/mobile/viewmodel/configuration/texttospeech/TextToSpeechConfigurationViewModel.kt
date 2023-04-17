package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.update
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Action.TestRemoteHermesHttpTextToSpeechTest
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Change.*

@Stable
class TextToSpeechConfigurationViewModel(
    service: TextToSpeechService
) : IConfigurationViewModel<TextToSpeechConfigurationViewState>(
    service = service,
    initialViewState = ::TextToSpeechConfigurationViewState
) {

    fun onEvent(event: TextToSpeechConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        contentViewState.update {
            when (change) {
                is SelectTextToSpeechOption -> it.copy(textToSpeechOption = change.option)
                is SetUseCustomHttpEndpoint -> it.copy(isUseCustomTextToSpeechHttpEndpoint = change.enabled)
                is UpdateTextToSpeechHttpEndpoint -> it.copy(textToSpeechHttpEndpoint = change.endpoint)
                is UpdateTestTextToSpeechText -> it.copy(testTextToSpeechText = change.text)
            }
        }
    }

    private fun onAction(action: Action) {
        when(action) {
            TestRemoteHermesHttpTextToSpeechTest -> startTextToSpeech()
        }
    }

    override fun onSave() {
        ConfigurationSetting.textToSpeechOption.value = data.textToSpeechOption
        ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value = data.isUseCustomTextToSpeechHttpEndpoint
        ConfigurationSetting.textToSpeechHttpEndpoint.value = data.textToSpeechHttpEndpoint
    }

    override fun initializeTestParams() {
        get<TextToSpeechServiceParams> {
            parametersOf(
                TextToSpeechServiceParams(
                    textToSpeechOption = data.textToSpeechOption
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomTextToSpeechHttpEndpoint = data.isUseCustomTextToSpeechHttpEndpoint,
                    textToSpeechHttpEndpoint = data.textToSpeechHttpEndpoint
                )
            )
        }
    }

    private fun startTextToSpeech() {
        testScope.launch {
            //await for mqtt
            get<MqttService>()
                .isHasStarted
                .map { it }
                .distinctUntilChanged()
                .first { it }

            get<TextToSpeechService>().textToSpeech("", data.testTextToSpeechText)
        }
    }

}