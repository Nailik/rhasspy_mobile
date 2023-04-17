package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.logic.services.recording.RecordingService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.update
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action.TestSpeechToTextToggleRecording
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SelectSpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SetUseCustomHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SetUseSpeechToTextMqttSilenceDetection
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.UpdateSpeechToTextHttpEndpoint

@Stable
class SpeechToTextConfigurationViewModel(
    service: SpeechToTextService
) : IConfigurationViewModel<SpeechToTextConfigurationViewState>(
    service = service,
    initialViewState = ::SpeechToTextConfigurationViewState
) {

    fun onEvent(event: SpeechToTextConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        contentViewState.update {
            when (change) {
                is SelectSpeechToTextOption -> it.copy(speechToTextOption = change.option)
                is SetUseCustomHttpEndpoint -> it.copy(isUseCustomSpeechToTextHttpEndpoint = change.enabled)
                is SetUseSpeechToTextMqttSilenceDetection -> it.copy(isUseSpeechToTextMqttSilenceDetection = change.enabled)
                is UpdateSpeechToTextHttpEndpoint -> it.copy(speechToTextHttpEndpoint = change.endpoint)
            }
        }
    }

    private fun onAction(action: Action) {
        when(action) {
            TestSpeechToTextToggleRecording -> toggleRecording()
        }
    }

    override fun onSave() {
        ConfigurationSetting.speechToTextOption.value = data.speechToTextOption
        ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value = data.isUseCustomSpeechToTextHttpEndpoint
        ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value = data.isUseSpeechToTextMqttSilenceDetection
        ConfigurationSetting.speechToTextHttpEndpoint.value = data.speechToTextHttpEndpoint
    }

    override fun initializeTestParams() {
        get<MqttServiceParams> {
            parametersOf(
                MqttServiceParams(
                    isUseSpeechToTextMqttSilenceDetection = data.isUseSpeechToTextMqttSilenceDetection
                )
            )
        }

        get<SpeechToTextServiceParams> {
            parametersOf(
                SpeechToTextServiceParams(
                    speechToTextOption = data.speechToTextOption,
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomSpeechToTextHttpEndpoint = data.isUseCustomSpeechToTextHttpEndpoint,
                    speechToTextHttpEndpoint = data.speechToTextHttpEndpoint
                )
            )
        }
    }

    private fun toggleRecording() {
        testScope.launch {
            if (get<SpeechToTextServiceParams>().speechToTextOption == SpeechToTextOption.RemoteMQTT) {
                //await for mqtt service to start if necessary
                get<MqttService>()
                    .isHasStarted
                    .map { it }
                    .distinctUntilChanged()
                    .first { it }
            }

            if (!get<RecordingService>().isRecording.value) {
                println("not yet recording start")
                //start recording
                get<SpeechToTextService>().startSpeechToText("", false)
            } else {
                println("is recording, stop")
                //stop recording
                get<SpeechToTextService>().endSpeechToText("", false)
            }
        }
    }

}