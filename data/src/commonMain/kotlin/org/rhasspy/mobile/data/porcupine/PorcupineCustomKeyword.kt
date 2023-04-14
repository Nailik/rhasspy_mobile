package org.rhasspy.mobile.data.porcupine

import kotlinx.serialization.Serializable

@Serializable
data class PorcupineCustomKeyword(
    val fileName: String,
    override val isEnabled: Boolean,
    override val sensitivity: Float
) : PorcupineKeyword

