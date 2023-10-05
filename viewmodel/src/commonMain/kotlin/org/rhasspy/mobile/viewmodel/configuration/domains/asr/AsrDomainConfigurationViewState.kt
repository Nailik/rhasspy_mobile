package org.rhasspy.mobile.viewmodel.configuration.domains.asr

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.AsrDomainOption

@Stable
data class AsrDomainConfigurationViewState internal constructor(
    val editData: AsrDomainConfigurationData,
) {

    @Stable
    data class AsrDomainConfigurationData internal constructor(
        val asrDomainOption: AsrDomainOption,
        val isUseSpeechToTextMqttSilenceDetection: Boolean,
        val voiceTimeout: String,
        val mqttResultTimeout: String,
    ) {

        val asrDomainOptions: ImmutableList<AsrDomainOption> = AsrDomainOption.entries.toImmutableList()

    }

}