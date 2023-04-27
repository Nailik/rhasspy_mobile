package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.service.option.IOption

expect enum class AudioRecorderChannelType : IOption<AudioRecorderChannelType> {

    ;

    val value: Int

    companion object {
        val default: AudioRecorderChannelType
    }

}