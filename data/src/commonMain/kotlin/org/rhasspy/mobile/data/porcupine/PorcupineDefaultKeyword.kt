package org.rhasspy.mobile.data.porcupine

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.PorcupineKeywordOption

@Serializable
data class PorcupineDefaultKeyword(
    val option: PorcupineKeywordOption,
    val isEnabled: Boolean,
    val sensitivity: Float
)