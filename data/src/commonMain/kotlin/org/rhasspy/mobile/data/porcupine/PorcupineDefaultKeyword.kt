package org.rhasspy.mobile.data.porcupine

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.PorcupineKeywordOption

@Serializable
data class PorcupineDefaultKeyword(
    val option: PorcupineKeywordOption,
    override val isEnabled: Boolean,
    override val sensitivity: Double
) : PorcupineKeyword