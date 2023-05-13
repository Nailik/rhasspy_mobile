package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

actual enum class AudioRecorderEncodingType(
    override val text: StableStringResource,
    actual val value: Int,
    actual val bitRate: Int
) : IOption<AudioRecorderEncodingType> {

    //TODO("Not yet implemented")
    Default(MR.strings.defaultText.stable, 1, 1);

    override fun findValue(value: String): AudioRecorderEncodingType {
        return AudioRecorderEncodingType.valueOf(value)
    }

    actual companion object {
        actual val default: AudioRecorderEncodingType get() = Default
        actual fun supportedValues(): List<AudioRecorderEncodingType> {
            //TODO("Not yet implemented")
            return listOf()
        }
    }

}