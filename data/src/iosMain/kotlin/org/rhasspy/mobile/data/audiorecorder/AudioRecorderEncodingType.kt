package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.service.option.IOption

actual enum class AudioRecorderEncodingType(
    override val text: StableStringResource,
    actual val value: Int,
    actual val bitRate: Int
) : IOption<AudioRecorderEncodingType> {
    ;

    actual companion object {
        actual val default: AudioRecorderEncodingType get() = TODO()
        actual fun supportedValues(): List<AudioRecorderEncodingType> {
            TODO("Not yet implemented")
        }
    }

}