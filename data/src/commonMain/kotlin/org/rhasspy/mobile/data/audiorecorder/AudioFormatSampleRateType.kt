package org.rhasspy.mobile.data.audiorecorder

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.IOption

@Serializable
expect enum class AudioFormatSampleRateType : IOption {

    ;

    val value: Int

    companion object {
        val default: AudioFormatSampleRateType
    }

}