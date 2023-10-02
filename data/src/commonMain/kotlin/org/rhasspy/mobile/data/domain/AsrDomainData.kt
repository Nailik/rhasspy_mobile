package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.AsrDomainOption
import kotlin.time.Duration

@Serializable
data class AsrDomainData(
    val option: AsrDomainOption,
    val isUseSpeechToTextMqttSilenceDetection: Boolean,
    val voiceTimeout: Duration,
    val mqttResultTimeout: Duration,
)