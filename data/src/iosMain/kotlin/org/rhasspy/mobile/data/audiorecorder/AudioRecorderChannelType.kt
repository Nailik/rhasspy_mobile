package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.service.option.IOption

actual enum class AudioRecorderChannelType(
    override val text: StableStringResource,
    actual val value: Int
) : IOption<AudioRecorderChannelType> {
    ;

    actual companion object {
        actual val default: AudioRecorderChannelType get() = TODO()
    }
}