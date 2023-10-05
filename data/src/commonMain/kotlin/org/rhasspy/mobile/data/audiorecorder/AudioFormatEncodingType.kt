package org.rhasspy.mobile.data.audiorecorder

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.IOption

@Serializable
expect enum class AudioFormatEncodingType : IOption {

    ;

    val value: Int
    val bitRate: Int

    companion object {
        val default: AudioFormatEncodingType

        fun supportedValues(): List<AudioFormatEncodingType>
    }

}