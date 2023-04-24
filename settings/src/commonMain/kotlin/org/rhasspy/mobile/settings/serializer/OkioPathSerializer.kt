package org.rhasspy.mobile.settings.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import okio.Path
import okio.Path.Companion.toPath

internal object OkioPathSerializer : KSerializer<Path?> {

    @OptIn(InternalSerializationApi::class)
    private val delegatedSerializer = String::class.serializer()

    override val descriptor =
        PrimitiveSerialDescriptor("OkioPathSerializer", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Path?) {
        encoder.encodeNullableSerializableValue(delegatedSerializer, value?.toString())
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Path? {
        return decoder.decodeNullableSerializableValue(delegatedSerializer)?.toPath()
    }
}