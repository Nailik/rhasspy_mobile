package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.SpeechToTextOption

@Serializable
data class AsrDomainData(
    val option: SpeechToTextOption,
    val isUseSpeechToTextMqttSilenceDetection: Boolean,
)