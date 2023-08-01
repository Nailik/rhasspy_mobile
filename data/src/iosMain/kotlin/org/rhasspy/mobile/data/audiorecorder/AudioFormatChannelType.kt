package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

actual enum class AudioFormatChannelType(
    override val text: StableStringResource,
    actual val value: Int,
    actual val count: Int
) : IOption<AudioFormatChannelType> {
    //TODO("Not yet implemented")
    Default(MR.strings.defaultText.stable, 1, 1);

    override fun findValue(value: String): AudioFormatChannelType {
        return AudioFormatChannelType.valueOf(value)
    }

    actual companion object {
        actual val default: AudioFormatChannelType get() = Default
    }

}