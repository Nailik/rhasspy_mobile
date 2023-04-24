package org.rhasspy.mobile.data.porcupine

import androidx.compose.runtime.Stable

@Stable
interface PorcupineKeyword {
    val isEnabled: Boolean
    val sensitivity: Float
}