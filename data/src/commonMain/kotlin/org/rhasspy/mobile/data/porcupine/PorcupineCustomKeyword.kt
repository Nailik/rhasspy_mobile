package org.rhasspy.mobile.data.porcupine

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class PorcupineCustomKeyword(
    val fileName: String,
    override val isEnabled: Boolean,
    override val sensitivity: Float,
) : PorcupineKeyword

