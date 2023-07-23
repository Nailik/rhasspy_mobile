package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class IntentHandlingConfigurationViewState internal constructor(
    override val editData: IntentHandlingConfigurationData
) : IConfigurationViewState {

    @Stable
    data class IntentHandlingConfigurationData internal constructor(
        val intentHandlingOption: IntentHandlingOption = ConfigurationSetting.intentHandlingOption.value,
        val intentHandlingHttpEndpoint: String = ConfigurationSetting.intentHandlingHttpEndpoint.value,
        val intentHandlingHomeAssistantEndpoint: String = ConfigurationSetting.intentHandlingHomeAssistantEndpoint.value,
        val intentHandlingHomeAssistantAccessToken: String = ConfigurationSetting.intentHandlingHomeAssistantAccessToken.value,
        val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption = ConfigurationSetting.intentHandlingHomeAssistantOption.value,
    ) : IConfigurationData {

        val intentHandlingOptionList: ImmutableList<IntentHandlingOption> =
            IntentHandlingOption.values().toList().toImmutableList()

    }

}