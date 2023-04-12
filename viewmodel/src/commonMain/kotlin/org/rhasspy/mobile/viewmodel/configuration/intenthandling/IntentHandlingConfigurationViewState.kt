package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class IntentHandlingConfigurationViewState(
    val intentHandlingOptionList: ImmutableList<IntentHandlingOption> = IntentHandlingOption.values().toList().toImmutableList(),
    val intentHandlingOption: IntentHandlingOption = ConfigurationSetting.intentHandlingOption.value,
    val intentHandlingHttpEndpoint: String = ConfigurationSetting.intentHandlingHttpEndpoint.value,
    val intentHandlingHassEndpoint: String = ConfigurationSetting.intentHandlingHassEndpoint.value,
    val intentHandlingHassAccessToken: String = ConfigurationSetting.intentHandlingHassAccessToken.value,
    val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption = ConfigurationSetting.intentHandlingHomeAssistantOption.value
): IConfigurationEditViewState {

    override val hasUnsavedChanges: Boolean
        get() = !(intentHandlingOption == ConfigurationSetting.intentHandlingOption.value &&
                intentHandlingHttpEndpoint == ConfigurationSetting.intentHandlingHttpEndpoint.value &&
                intentHandlingHassEndpoint == ConfigurationSetting.intentHandlingHassEndpoint.value &&
                intentHandlingHassAccessToken == ConfigurationSetting.intentHandlingHassAccessToken.value &&
                intentHandlingHomeAssistantOption == ConfigurationSetting.intentHandlingHomeAssistantOption.value)

    override val isTestingEnabled: Boolean get() = intentHandlingOption != IntentHandlingOption.Disabled

}