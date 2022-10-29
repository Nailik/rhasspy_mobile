package org.rhasspy.mobile.settings.porcupine

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import org.rhasspy.mobile.data.PorcupineKeywordOptions

@Serializable
data class PorcupineCustomKeyword(
    val fileName: String,
    val enabled: Boolean,
    val sensitivity: Float
)

