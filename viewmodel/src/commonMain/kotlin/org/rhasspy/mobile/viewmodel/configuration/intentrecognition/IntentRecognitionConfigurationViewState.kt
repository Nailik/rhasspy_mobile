package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import kotlin.time.Duration

@Stable
data class IntentRecognitionConfigurationViewState internal constructor(
    val editData: IntentRecognitionConfigurationData
) {

    @Stable
    data class IntentRecognitionConfigurationData internal constructor(
        val intentRecognitionOption: IntentRecognitionOption,
        val isRhasspy2HermesHttpHandleWithRecognition: Boolean,
        val rhasspy2HermesHttpHandleTimeout: Duration,
        val rhasspy2HermesMqttHandleTimeout: Duration,
    ) {

        val intentRecognitionOptionList: ImmutableList<IntentRecognitionOption> = IntentRecognitionOption.entries.toImmutableList()

    }

}