package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.ServiceStateHeaderViewState

@Stable
data class SpeechToTextConfigurationViewState(
    val speechToTextOption: SpeechToTextOption,
    val isUseCustomSpeechToTextHttpEndpoint: Boolean,
    val isUseSpeechToTextMqttSilenceDetection: Boolean,
    val speechToTextHttpEndpoint: String
): IConfigurationContentViewState() {

    companion object {
        fun getInitial() = SpeechToTextConfigurationViewState(
            speechToTextOption = ConfigurationSetting.speechToTextOption.value,
            isUseCustomSpeechToTextHttpEndpoint = ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value,
            isUseSpeechToTextMqttSilenceDetection = ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value,
            speechToTextHttpEndpoint = ConfigurationSetting.speechToTextHttpEndpoint.value
        )
    }

    override fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>): IConfigurationEditViewState {
        return IConfigurationEditViewState(
            hasUnsavedChanges = !(speechToTextOption == ConfigurationSetting.speechToTextOption.value &&
                isUseCustomSpeechToTextHttpEndpoint == ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value &&
                isUseSpeechToTextMqttSilenceDetection == ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value &&
                speechToTextHttpEndpoint == ConfigurationSetting.speechToTextHttpEndpoint.value),
            isTestingEnabled = speechToTextOption != SpeechToTextOption.Disabled,
            serviceViewState = serviceViewState)
    }

    override fun save() {
        ConfigurationSetting.speechToTextOption.value = speechToTextOption
        ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value = isUseCustomSpeechToTextHttpEndpoint
        ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value = isUseSpeechToTextMqttSilenceDetection
        ConfigurationSetting.speechToTextHttpEndpoint.value = speechToTextHttpEndpoint
    }

}