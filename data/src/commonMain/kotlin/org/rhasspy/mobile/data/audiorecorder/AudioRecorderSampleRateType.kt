package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.service.option.IOption

expect enum class AudioRecorderSampleRateType : IOption<AudioRecorderSampleRateType> {

    ;

    val value: Int

    companion object {
        val default: AudioRecorderSampleRateType
    }

}