package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.service.option.IOption

expect enum class AudioFormatSampleRateType : IOption<AudioFormatSampleRateType> {

    ;

    val value: Int

    companion object {
        val default: AudioFormatSampleRateType
    }

}