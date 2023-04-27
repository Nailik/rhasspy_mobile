package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.service.option.IOption

expect enum class AudioRecorderEncodingType : IOption<AudioRecorderEncodingType> {

    ;

    val value: Int
    val bitRate: Int

    companion object {
        val default: AudioRecorderEncodingType
        fun supportedValues(): List<AudioRecorderEncodingType>
    }

}