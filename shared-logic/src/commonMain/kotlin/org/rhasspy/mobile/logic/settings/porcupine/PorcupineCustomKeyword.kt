package org.rhasspy.mobile.logic.settings.porcupine

import kotlinx.serialization.Serializable

@Serializable
data class PorcupineCustomKeyword(
    val fileName: String,
    val isEnabled: Boolean,
    val sensitivity: Float
)

