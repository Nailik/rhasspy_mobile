package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption

@Serializable
data class SndDomainData(
    val option: AudioPlayingOption,
    val localOutputOption: AudioOutputOption,
    val mqttSiteId: String,
)