package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.TextToSpeechOption

@Serializable
data class TtsDomainData(
    val option: TextToSpeechOption
)