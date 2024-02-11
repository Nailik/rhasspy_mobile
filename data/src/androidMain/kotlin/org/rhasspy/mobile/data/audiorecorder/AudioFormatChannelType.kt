package org.rhasspy.mobile.data.audiorecorder

import android.media.AudioFormat.CHANNEL_IN_MONO
import android.media.AudioFormat.CHANNEL_IN_STEREO
import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

@Serializable
actual enum class AudioFormatChannelType(
    override val text: StableStringResource,
    actual val value: Int,
    actual val count: Int
) : IOption {

    Mono(MR.strings.channel_type_mono.stable, CHANNEL_IN_MONO, 1),
    Stereo(MR.strings.channel_type_stereo.stable, CHANNEL_IN_STEREO, 2);

    actual companion object {
        actual val default: AudioFormatChannelType get() = Mono
    }
}