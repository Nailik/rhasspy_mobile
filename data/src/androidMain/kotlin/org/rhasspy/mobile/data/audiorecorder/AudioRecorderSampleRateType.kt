package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption

actual enum class AudioRecorderSampleRateType(
    override val text: StableStringResource,
    actual val value: Int
) : IOption<AudioRecorderSampleRateType> {

    SR11025(MR.strings.sample_rate_type_11025.stable, 11025),
    SR22050(MR.strings.sample_rate_type_22050.stable, 22050),
    SR16000(MR.strings.sample_rate_type_16000.stable, 16000),
    SR32000(MR.strings.sample_rate_type_32000.stable, 32000),
    SR44100(MR.strings.sample_rate_type_44100.stable, 44100),
    SR48000(MR.strings.sample_rate_type_48000.stable, 48000);

    override fun findValue(value: String): AudioRecorderSampleRateType {
        return AudioRecorderSampleRateType.valueOf(value)
    }

    actual companion object {
        actual val default: AudioRecorderSampleRateType get() = SR16000
    }
}