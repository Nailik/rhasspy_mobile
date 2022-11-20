package org.rhasspy.mobile.settings.porcupine

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.PorcupineKeywordOptions

@Serializable
data class PorcupineDefaultKeyword(
    val option: PorcupineKeywordOptions,
    val isEnabled: Boolean,
    val sensitivity: Float
)