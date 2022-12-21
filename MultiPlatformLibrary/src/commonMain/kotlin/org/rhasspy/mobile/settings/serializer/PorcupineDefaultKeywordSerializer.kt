package org.rhasspy.mobile.settings.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import org.rhasspy.mobile.settings.porcupine.PorcupineDefaultKeyword

internal object PorcupineDefaultKeywordSerializer : KSerializer<Set<PorcupineDefaultKeyword>> {

    @OptIn(InternalSerializationApi::class)
    private val delegatedSerializer = ListSerializer(PorcupineDefaultKeyword::class.serializer())

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor =
        SerialDescriptor("PorcupineDefaultKeywordSerializer", delegatedSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Set<PorcupineDefaultKeyword>) {
        val l = value.toList()
        encoder.encodeSerializableValue(delegatedSerializer, l)
    }

    override fun deserialize(decoder: Decoder): Set<PorcupineDefaultKeyword> {
        return decoder.decodeSerializableValue(delegatedSerializer).toSet()
    }
}