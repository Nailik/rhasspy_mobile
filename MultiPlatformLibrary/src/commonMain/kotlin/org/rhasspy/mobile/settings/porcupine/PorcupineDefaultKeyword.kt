package org.rhasspy.mobile.settings.porcupine

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.PorcupineKeywordOptions

@Serializable
data class PorcupineDefaultKeyword(
    val option: PorcupineKeywordOptions,
    val enabled: Boolean,
    val sensitivity: Float
)