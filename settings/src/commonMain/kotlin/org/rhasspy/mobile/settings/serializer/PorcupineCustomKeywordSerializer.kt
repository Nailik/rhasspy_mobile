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
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword

internal object PorcupineCustomKeywordSerializer : KSerializer<ImmutableList<PorcupineCustomKeyword>> {

    @OptIn(InternalSerializationApi::class)
    private val delegatedSerializer = ListSerializer(PorcupineCustomKeyword::class.serializer())

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor =
        SerialDescriptor("PorcupineCustomKeywordSerializer", delegatedSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: ImmutableList<PorcupineCustomKeyword>) {
        encoder.encodeSerializableValue(delegatedSerializer, value.toList())
    }

    override fun deserialize(decoder: Decoder): ImmutableList<PorcupineCustomKeyword> {
        return decoder.decodeSerializableValue(delegatedSerializer).toImmutableList()
    }
}