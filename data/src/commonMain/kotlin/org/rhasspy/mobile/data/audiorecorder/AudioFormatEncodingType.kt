package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.service.option.IOption

expect enum class AudioFormatEncodingType : IOption<AudioFormatEncodingType> {

    ;

    val value: Int
    val bitRate: Int

    companion object {
        val default: AudioFormatEncodingType
        val porcupine: AudioFormatEncodingType
        fun supportedValues(): List<AudioFormatEncodingType>
    }

}