package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.ServiceStateHeaderViewState

@Stable
data class IntentHandlingConfigurationViewState(
    val intentHandlingOptionList: ImmutableList<IntentHandlingOption>,
    val intentHandlingOption: IntentHandlingOption,
    val intentHandlingHttpEndpoint: String,
    val intentHandlingHassEndpoint: String,
    val intentHandlingHassAccessToken: String,
    val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption,
): IConfigurationContentViewState() {

    companion object {
        fun getInitial() = IntentHandlingConfigurationViewState(
            intentHandlingOptionList= IntentHandlingOption.values().toList().toImmutableList(),
            intentHandlingOption = ConfigurationSetting.intentHandlingOption.value,
            intentHandlingHttpEndpoint = ConfigurationSetting.intentHandlingHttpEndpoint.value,
            intentHandlingHassEndpoint = ConfigurationSetting.intentHandlingHassEndpoint.value,
            intentHandlingHassAccessToken = ConfigurationSetting.intentHandlingHassAccessToken.value,
            intentHandlingHomeAssistantOption = ConfigurationSetting.intentHandlingHomeAssistantOption.value
        )
    }

    override fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>): IConfigurationEditViewState {
        return IConfigurationEditViewState(
            hasUnsavedChanges = !(intentHandlingOption == ConfigurationSetting.intentHandlingOption.value &&
                intentHandlingHttpEndpoint == ConfigurationSetting.intentHandlingHttpEndpoint.value &&
                intentHandlingHassEndpoint == ConfigurationSetting.intentHandlingHassEndpoint.value &&
                intentHandlingHassAccessToken == ConfigurationSetting.intentHandlingHassAccessToken.value &&
                intentHandlingHomeAssistantOption == ConfigurationSetting.intentHandlingHomeAssistantOption.value),
            isTestingEnabled = intentHandlingOption != IntentHandlingOption.WithRecognition &&
                    intentHandlingOption != IntentHandlingOption.Disabled,
            serviceViewState = serviceViewState
        )
    }

    override fun save() {
        ConfigurationSetting.intentHandlingOption.value = intentHandlingOption
        ConfigurationSetting.intentHandlingHttpEndpoint.value = intentHandlingHttpEndpoint
        ConfigurationSetting.intentHandlingHassEndpoint.value = intentHandlingHassEndpoint
        ConfigurationSetting.intentHandlingHassAccessToken.value = intentHandlingHassAccessToken
        ConfigurationSetting.intentHandlingHomeAssistantOption.value = intentHandlingHomeAssistantOption
    }

}