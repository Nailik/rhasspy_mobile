package org.rhasspy.mobile.data.audiorecorder

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.IOption

@Serializable
expect enum class AudioSourceType : IOption {

    ;

    val value: Int

    companion object {
        val default: AudioSourceType
        fun supportedValues(): List<AudioSourceType>
    }

}