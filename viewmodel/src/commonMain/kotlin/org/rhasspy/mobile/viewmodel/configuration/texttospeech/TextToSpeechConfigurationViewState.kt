package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState.IConfigurationData
import kotlin.time.Duration

@Stable
data class TextToSpeechConfigurationViewState internal constructor(
    override val editData: TextToSpeechConfigurationData
) : IConfigurationViewState {

    @Stable
    data class TextToSpeechConfigurationData(
        val textToSpeechOption: TextToSpeechOption,
        val rhasspy2HermesMqttTimeout: Duration,
    ) : IConfigurationData {

        val textToSpeechOptions: ImmutableList<TextToSpeechOption> = TextToSpeechOption.entries.toImmutableList()

    }

}