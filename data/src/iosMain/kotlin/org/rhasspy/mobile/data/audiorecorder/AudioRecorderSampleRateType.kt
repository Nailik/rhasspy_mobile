package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.service.option.IOption

actual enum class AudioRecorderSampleRateType(
    override val text: StableStringResource,
    actual val value: Int
) : IOption<AudioRecorderSampleRateType> {
    ;

    actual companion object {
        actual val default: AudioRecorderSampleRateType get() = TODO()
    }
}