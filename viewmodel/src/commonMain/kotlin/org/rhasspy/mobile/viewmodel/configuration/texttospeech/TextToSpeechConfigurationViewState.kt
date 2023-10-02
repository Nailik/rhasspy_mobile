package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.TtsDomainOption
import kotlin.time.Duration

@Stable
data class TextToSpeechConfigurationViewState internal constructor(
    val editData: TextToSpeechConfigurationData
) {

    @Stable
    data class TextToSpeechConfigurationData(
        val ttsDomainOption: TtsDomainOption,
        val rhasspy2HermesMqttTimeout: Duration,
    ) {

        val ttsDomainOptions: ImmutableList<TtsDomainOption> = TtsDomainOption.entries.toImmutableList()

    }

}