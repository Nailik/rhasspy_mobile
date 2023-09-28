package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData
import kotlin.time.Duration

@Stable
data class SpeechToTextConfigurationViewState internal constructor(
    override val editData: SpeechToTextConfigurationData,
) : IConfigurationViewState {

    @Stable
    data class SpeechToTextConfigurationData internal constructor(
        val speechToTextOption: SpeechToTextOption,
        val isUseSpeechToTextMqttSilenceDetection: Boolean,
        val mqttTimeout: Duration,
    ) : IConfigurationData {

        val speechToTextOptions: ImmutableList<SpeechToTextOption> = SpeechToTextOption.entries.toImmutableList()

    }

}