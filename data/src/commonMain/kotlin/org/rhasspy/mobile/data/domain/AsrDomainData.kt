package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import kotlin.time.Duration

@Serializable
data class AsrDomainData(
    val option: SpeechToTextOption,
    val isUseSpeechToTextMqttSilenceDetection: Boolean,
    val mqttTimeout: Duration,
)