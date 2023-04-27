package org.rhasspy.mobile.data.audiorecorder

import android.media.AudioFormat.*
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption

actual enum class AudioRecorderChannelType(
    override val text: StableStringResource,
    actual val value: Int
) : IOption<AudioRecorderChannelType> {

    Default(MR.strings.channel_type_default.stable, CHANNEL_IN_DEFAULT),
    Mono(MR.strings.channel_type_mono.stable, CHANNEL_IN_MONO),
    Stereo(MR.strings.channel_type_stereo.stable, CHANNEL_IN_STEREO),
    Left(MR.strings.channel_type_left.stable, CHANNEL_IN_LEFT),
    Right(MR.strings.channel_type_right.stable, CHANNEL_IN_RIGHT);

    override fun findValue(value: String): AudioRecorderChannelType {
        return AudioRecorderChannelType.valueOf(value)
    }

    actual companion object {
        actual val default: AudioRecorderChannelType get() = Default
    }
}