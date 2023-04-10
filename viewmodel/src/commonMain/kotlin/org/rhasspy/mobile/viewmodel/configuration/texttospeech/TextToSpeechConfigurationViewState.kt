package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.ServiceStateHeaderViewState

@Stable
data class TextToSpeechConfigurationViewState(
    val textToSpeechOption: TextToSpeechOption,
    val isUseCustomTextToSpeechHttpEndpoint: Boolean,
    val textToSpeechHttpEndpoint: String
): IConfigurationContentViewState() {

    companion object {
        fun getInitial() = TextToSpeechConfigurationViewState(
            textToSpeechOption = ConfigurationSetting.textToSpeechOption.value,
            isUseCustomTextToSpeechHttpEndpoint = ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value,
            textToSpeechHttpEndpoint = ConfigurationSetting.textToSpeechHttpEndpoint.value
        )
    }

    override fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>): IConfigurationEditViewState {
        return IConfigurationEditViewState(
            hasUnsavedChanges = !(textToSpeechOption == ConfigurationSetting.textToSpeechOption.value &&
                    isUseCustomTextToSpeechHttpEndpoint == ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value &&
            textToSpeechHttpEndpoint == ConfigurationSetting.textToSpeechHttpEndpoint.value),
            isTestingEnabled = textToSpeechOption != TextToSpeechOption.Disabled,
            serviceViewState = serviceViewState
        )
    }

    override fun save() {
        ConfigurationSetting.textToSpeechOption.value = textToSpeechOption
        ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value = isUseCustomTextToSpeechHttpEndpoint
        ConfigurationSetting.textToSpeechHttpEndpoint.value = textToSpeechHttpEndpoint
    }

}