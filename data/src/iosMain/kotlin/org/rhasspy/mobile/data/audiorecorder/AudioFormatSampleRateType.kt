package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

actual enum class AudioFormatSampleRateType(
    override val text: StableStringResource,
    actual val value: Int,
) : IOption<AudioFormatSampleRateType> {
    //TODO("Not yet implemented")
    Default(MR.strings.defaultText.stable, 1);

    override fun findValue(value: String): AudioFormatSampleRateType {
        return AudioFormatSampleRateType.valueOf(value)
    }

    actual companion object {
        actual val default: AudioFormatSampleRateType get() = Default
    }
}