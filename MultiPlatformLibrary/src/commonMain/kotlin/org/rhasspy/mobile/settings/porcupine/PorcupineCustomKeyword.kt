package org.rhasspy.mobile.settings.porcupine

import kotlinx.serialization.Serializable

@Serializable
data class PorcupineCustomKeyword(
    val fileName: String,
    val isEnabled: Boolean,
    val sensitivity: Float
)

