package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

actual enum class AudioFormatSampleRateType(
    override val text: StableStringResource,
    actual val value: Int
) : IOption<AudioFormatSampleRateType> {

    SR11025(MR.strings.sample_rate_type_11025.stable, 11025),
    SR12000(MR.strings.sample_rate_type_12000.stable, 12000),
    SR16000(MR.strings.sample_rate_type_16000.stable, 16000),
    SR22050(MR.strings.sample_rate_type_22050.stable, 22050),
    SR32000(MR.strings.sample_rate_type_32000.stable, 32000),
    SR44100(MR.strings.sample_rate_type_44100.stable, 44100),
    SR48000(MR.strings.sample_rate_type_48000.stable, 48000);

    override fun findValue(value: String): AudioFormatSampleRateType {
        return AudioFormatSampleRateType.valueOf(value)
    }

    actual companion object {
        actual val default: AudioFormatSampleRateType get() = SR16000

        fun findValue(value: Int): AudioFormatSampleRateType {
            return AudioFormatSampleRateType.values().firstOrNull { it.value == value } ?: default
        }
    }
}