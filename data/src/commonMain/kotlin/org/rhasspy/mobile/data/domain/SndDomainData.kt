package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import kotlin.time.Duration

@Serializable
data class SndDomainData(
    val option: AudioPlayingOption,
    val localOutputOption: AudioOutputOption,
    val mqttSiteId: String,
    val audioTimeout: Duration,
    val rhasspy2HermesMqttTimeout: Duration,
)