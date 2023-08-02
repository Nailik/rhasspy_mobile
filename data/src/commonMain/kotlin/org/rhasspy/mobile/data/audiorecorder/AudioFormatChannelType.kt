package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.service.option.IOption

expect enum class AudioFormatChannelType : IOption<AudioFormatChannelType> {

    ;

    val value: Int
    val count: Int

    companion object {
        val default: AudioFormatChannelType
    }

}