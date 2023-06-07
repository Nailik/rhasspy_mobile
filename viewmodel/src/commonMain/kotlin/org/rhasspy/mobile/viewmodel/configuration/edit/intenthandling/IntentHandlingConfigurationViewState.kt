package org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewState

@Stable
data class IntentHandlingConfigurationViewState internal constructor(
    val intentHandlingOption: IntentHandlingOption = ConfigurationSetting.intentHandlingOption.value,
    val intentHandlingHttpEndpoint: String = ConfigurationSetting.intentHandlingHttpEndpoint.value,
    val intentHandlingHassEndpoint: String = ConfigurationSetting.intentHandlingHassEndpoint.value,
    val intentHandlingHassAccessToken: String = ConfigurationSetting.intentHandlingHassAccessToken.value,
    val intentHandlingHassOption: HomeAssistantIntentHandlingOption = ConfigurationSetting.intentHandlingHomeAssistantOption.value,
    val testIntentHandlingName: String = "",
    val testIntentHandlingText: String = ""
) : IConfigurationEditViewState() {

    val intentHandlingOptionList: ImmutableList<IntentHandlingOption> = IntentHandlingOption.values().toList().toImmutableList()

    override val isTestingEnabled: Boolean get() = intentHandlingOption != IntentHandlingOption.Disabled

}