package org.rhasspy.mobile.logic.settings.porcupine

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.logic.settings.option.PorcupineKeywordOption

@Serializable
data class PorcupineDefaultKeyword(
    val option: PorcupineKeywordOption,
    val isEnabled: Boolean,
    val sensitivity: Float
)