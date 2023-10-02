package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.HandleDomainOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import kotlin.time.Duration

@Stable
data class IntentHandlingConfigurationViewState internal constructor(
    val editData: IntentHandlingConfigurationData
) {

    @Stable
    data class IntentHandlingConfigurationData internal constructor(
        val handleDomainOption: HandleDomainOption,
        val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption,
        val homeAssistantEventTimeout: Duration,
    ) {

        val handleDomainOptionLists: ImmutableList<HandleDomainOption> = HandleDomainOption.entries.toImmutableList()

    }

}