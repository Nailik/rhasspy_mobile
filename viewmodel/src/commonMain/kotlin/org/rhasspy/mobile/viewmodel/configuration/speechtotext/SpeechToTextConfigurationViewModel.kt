package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.update
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiAction.Change
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiAction.Change.SelectSpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiAction.Change.ToggleUseCustomHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiAction.Change.ToggleUseSpeechToTextMqttSilenceDetection
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiAction.Change.UpdateSpeechToTextHttpEndpoint

@Stable
class SpeechToTextConfigurationViewModel(
    service: SpeechToTextService,
    testRunner: SpeechToTextConfigurationTest
) : IConfigurationViewModel<SpeechToTextConfigurationTest, SpeechToTextConfigurationViewState>(
    service = service,
    testRunner = testRunner,
    initialViewState = ::SpeechToTextConfigurationViewState
) {

    fun onAction(action: SpeechToTextConfigurationUiAction) {
        when(action) {
            is Change -> onChange(action)
        }
    }

    private fun onChange(change: Change) {
        contentViewState.update {
            when (change) {
                is SelectSpeechToTextOption -> it.copy(speechToTextOption = change.option)
                ToggleUseCustomHttpEndpoint -> it.copy(isUseCustomSpeechToTextHttpEndpoint = !it.isUseCustomSpeechToTextHttpEndpoint)
                ToggleUseSpeechToTextMqttSilenceDetection -> it.copy(isUseSpeechToTextMqttSilenceDetection = !it.isUseSpeechToTextMqttSilenceDetection)
                is UpdateSpeechToTextHttpEndpoint ->  it.copy(speechToTextHttpEndpoint = change.value)
            }
        }
    }

    override fun onSave() {
        ConfigurationSetting.speechToTextOption.value = data.speechToTextOption
        ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value = data.isUseCustomSpeechToTextHttpEndpoint
        ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value = data.isUseSpeechToTextMqttSilenceDetection
        ConfigurationSetting.speechToTextHttpEndpoint.value = data.speechToTextHttpEndpoint
    }

    val isRecordingAudio get() = testRunner.isRecording

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

    fun toggleRecording() = testRunner.toggleRecording()

}