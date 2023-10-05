package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.IntentDomainOption

@Stable
data class IntentDomainConfigurationViewState internal constructor(
    val editData: IntentDomainConfigurationData
) {

    @Stable
    data class IntentDomainConfigurationData internal constructor(
        val intentDomainOption: IntentDomainOption,
        val isRhasspy2HermesHttpIntentHandleWithRecognition: Boolean,
        val rhasspy2HermesHttpIntentHandlingTimeout: String,
        val timeout: String,
    ) {

        val intentDomainOptionLists: ImmutableList<IntentDomainOption> = IntentDomainOption.entries.toImmutableList()

    }

}