package org.rhasspy.mobile.viewModels.settings.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import org.rhasspy.mobile.settings.porcupine.PorcupineCustomKeyword

internal object PorcupineCustomKeywordSerializer : KSerializer<Set<PorcupineCustomKeyword>> {
    @OptIn(InternalSerializationApi::class)
    private val delegatedSerializer = ListSerializer(PorcupineCustomKeyword::class.serializer())

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = SerialDescriptor("PorcupineCustomKeywordSerializer", delegatedSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Set<PorcupineCustomKeyword>) {
        val l = value.toList()
        encoder.encodeSerializableValue(delegatedSerializer, l)
    }

    override fun deserialize(decoder: Decoder): Set<PorcupineCustomKeyword> {
        return decoder.decodeSerializableValue(delegatedSerializer).toSet()
    }
}