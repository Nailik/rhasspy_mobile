package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class IntentRecognitionConfigurationViewState internal constructor(
    override val editData: IntentRecognitionConfigurationData
) : IConfigurationViewState {

    @Stable
    data class IntentRecognitionConfigurationData internal constructor(
        val intentRecognitionOption: IntentRecognitionOption,
    ) : IConfigurationData {

        val intentRecognitionOptionList: ImmutableList<IntentRecognitionOption> = IntentRecognitionOption.entries.toImmutableList()

    }

}