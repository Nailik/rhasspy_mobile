package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState.IConfigurationData
import kotlin.time.Duration

@Stable
data class IntentRecognitionConfigurationViewState internal constructor(
    override val editData: IntentRecognitionConfigurationData
) : IConfigurationViewState {

    @Stable
    data class IntentRecognitionConfigurationData internal constructor(
        val intentRecognitionOption: IntentRecognitionOption,
        val isRhasspy2HermesHttpHandleWithRecognition: Boolean,
        val rhasspy2HermesHttpHandleTimeout: Duration,
        val rhasspy2HermesMqttHandleTimeout: Duration,
    ) : IConfigurationData {

        val intentRecognitionOptionList: ImmutableList<IntentRecognitionOption> = IntentRecognitionOption.entries.toImmutableList()

    }

}