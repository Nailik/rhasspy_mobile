package org.rhasspy.mobile.settings.serializer

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

internal object StringListSerializer : KSerializer<ImmutableList<String>> {

    @OptIn(InternalSerializationApi::class)
    private val delegatedSerializer = ListSerializer(String::class.serializer())

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor =
        SerialDescriptor("StringSetSerializer", delegatedSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: ImmutableList<String>) {
        encoder.encodeSerializableValue(delegatedSerializer, value.toList())
    }

    override fun deserialize(decoder: Decoder): ImmutableList<String> {
        return decoder.decodeSerializableValue(delegatedSerializer).toImmutableList()
    }
}