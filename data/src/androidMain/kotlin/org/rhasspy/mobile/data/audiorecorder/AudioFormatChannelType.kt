package org.rhasspy.mobile.data.audiorecorder

import android.media.AudioFormat.CHANNEL_IN_MONO
import android.media.AudioFormat.CHANNEL_IN_STEREO
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

actual enum class AudioFormatChannelType(
    override val text: StableStringResource,
    actual val value: Int,
    actual val count: Int,
) : IOption<AudioFormatChannelType> {

    Mono(MR.strings.channel_type_mono.stable, CHANNEL_IN_MONO, 1),
    Stereo(MR.strings.channel_type_stereo.stable, CHANNEL_IN_STEREO, 2);

    override fun findValue(value: String): AudioFormatChannelType {
        return AudioFormatChannelType.valueOf(value)
    }

    actual companion object {
        actual val default: AudioFormatChannelType get() = Mono
    }
}