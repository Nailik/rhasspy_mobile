package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import kotlin.time.Duration

@Stable
data class SpeechToTextConfigurationViewState internal constructor(
    val editData: SpeechToTextConfigurationData,
) {

    @Stable
    data class SpeechToTextConfigurationData internal constructor(
        val speechToTextOption: SpeechToTextOption,
        val isUseSpeechToTextMqttSilenceDetection: Boolean,
        val mqttTimeout: Duration,
    ) {

        val speechToTextOptions: ImmutableList<SpeechToTextOption> = SpeechToTextOption.entries.toImmutableList()

    }

}