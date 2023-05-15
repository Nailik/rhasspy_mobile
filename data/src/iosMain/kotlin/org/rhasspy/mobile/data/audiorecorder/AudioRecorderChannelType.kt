package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

actual enum class AudioRecorderChannelType(
    override val text: StableStringResource,
    actual val value: Int,
    actual val count: Int
) : IOption<AudioRecorderChannelType> {
    //TODO("Not yet implemented")
    Default(MR.strings.defaultText.stable, 1, 1);

    override fun findValue(value: String): AudioRecorderChannelType {
        return AudioRecorderChannelType.valueOf(value)
    }

    actual companion object {
        actual val default: AudioRecorderChannelType get() = Default
    }

}