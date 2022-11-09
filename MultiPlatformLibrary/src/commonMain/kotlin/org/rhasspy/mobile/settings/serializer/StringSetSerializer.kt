package org.rhasspy.mobile.settings.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

internal object StringSetSerializer : KSerializer<Set<String>> {

    @OptIn(InternalSerializationApi::class)
    private val delegatedSerializer = ListSerializer(String::class.serializer())
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = SerialDescriptor("StringSetSerializer", delegatedSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Set<String>) {
        val l = value.toList()
        encoder.encodeSerializableValue(delegatedSerializer, l)
    }

    override fun deserialize(decoder: Decoder): Set<String> {
        return decoder.decodeSerializableValue(delegatedSerializer).toSet()
    }
}