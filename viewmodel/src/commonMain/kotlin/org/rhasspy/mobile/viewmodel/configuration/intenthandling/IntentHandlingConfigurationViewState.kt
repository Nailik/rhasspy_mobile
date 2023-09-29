package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState.IConfigurationData
import kotlin.time.Duration

@Stable
data class IntentHandlingConfigurationViewState internal constructor(
    override val editData: IntentHandlingConfigurationData
) : IConfigurationViewState {

    @Stable
    data class IntentHandlingConfigurationData internal constructor(
        val intentHandlingOption: IntentHandlingOption,
        val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption,
        val homeAssistantEventTimeout: Duration,
    ) : IConfigurationData {

        val intentHandlingOptionList: ImmutableList<IntentHandlingOption> = IntentHandlingOption.entries.toImmutableList()

    }

}