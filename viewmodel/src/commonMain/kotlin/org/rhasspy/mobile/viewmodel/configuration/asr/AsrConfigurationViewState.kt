package org.rhasspy.mobile.viewmodel.configuration.asr

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.AsrDomainOption
import kotlin.time.Duration

@Stable
data class AsrConfigurationViewState internal constructor(
    val editData: AsrConfigurationData,
) {

    @Stable
    data class AsrConfigurationData internal constructor(
        val asrDomainOption: AsrDomainOption,
        val isUseSpeechToTextMqttSilenceDetection: Boolean,
        val voiceTimeout: Duration,
        val mqttResultTimeout: Duration,
    ) {

        val asrDomainOptions: ImmutableList<AsrDomainOption> = AsrDomainOption.entries.toImmutableList()
        val voiceTimeoutText: String = voiceTimeout.inWholeSeconds.toString()
        val mqttResultTimeoutText: String = mqttResultTimeout.inWholeSeconds.toString()

    }

}