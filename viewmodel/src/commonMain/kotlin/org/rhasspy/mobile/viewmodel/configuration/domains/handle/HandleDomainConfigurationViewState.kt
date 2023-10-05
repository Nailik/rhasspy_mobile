package org.rhasspy.mobile.viewmodel.configuration.domains.handle

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.HandleDomainOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption

@Stable
data class HandleDomainConfigurationViewState internal constructor(
    val editData: HandleDomainConfigurationData
) {

    @Stable
    data class HandleDomainConfigurationData internal constructor(
        val handleDomainOption: HandleDomainOption,
        val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption,
        val homeAssistantEventTimeout: String,
    ) {

        val handleDomainOptionLists: ImmutableList<HandleDomainOption> = HandleDomainOption.entries.toImmutableList()

    }

}