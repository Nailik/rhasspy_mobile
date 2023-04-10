package org.rhasspy.mobile.logic.settings.serializer

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword

internal object PorcupineDefaultKeywordSerializer : KSerializer<ImmutableSet<PorcupineDefaultKeyword>> {

    @OptIn(InternalSerializationApi::class)
    private val delegatedSerializer = ListSerializer(PorcupineDefaultKeyword::class.serializer())

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor =
        SerialDescriptor("PorcupineDefaultKeywordSerializer", delegatedSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: ImmutableSet<PorcupineDefaultKeyword>) {
        encoder.encodeSerializableValue(delegatedSerializer, value.toList())
    }

    override fun deserialize(decoder: Decoder): ImmutableSet<PorcupineDefaultKeyword> {
        return decoder.decodeSerializableValue(delegatedSerializer).toImmutableSet()
    }
}