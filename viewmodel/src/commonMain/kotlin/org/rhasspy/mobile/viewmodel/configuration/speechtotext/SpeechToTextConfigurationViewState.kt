package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class SpeechToTextConfigurationViewState internal constructor(
    override val editData: SpeechToTextConfigurationData,
) : IConfigurationViewState {

    @Stable
    data class SpeechToTextConfigurationData internal constructor(
        val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value,
        val isUseSpeechToTextMqttSilenceDetection: Boolean = ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value,
    ) : IConfigurationData {

        val speechToTextOptions: ImmutableList<SpeechToTextOption> = SpeechToTextOption.entries.toImmutableList()

    }

}