package org.rhasspy.mobile.data.audiorecorder

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.IOption

@Serializable
expect enum class AudioFormatChannelType : IOption {

    ;

    val value: Int
    val count: Int

    companion object {
        val default: AudioFormatChannelType
    }

}