package org.rhasspy.mobile.settings.porcupine

import kotlinx.serialization.Serializable

@Serializable
data class PorcupineCustomKeyword(
    val fileName: String,
    val enabled: Boolean,
    val sensitivity: Float
)

