package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

actual enum class AudioFormatEncodingType(
    override val text: StableStringResource,
    actual val value: Int,
    actual val bitRate: Int,
) : IOption<AudioFormatEncodingType> {

    //TODO("Not yet implemented")
    Default(MR.strings.defaultText.stable, 1, 1);

    override fun findValue(value: String): AudioFormatEncodingType {
        return AudioFormatEncodingType.valueOf(value)
    }

    actual companion object {
        actual val default: AudioFormatEncodingType = Default
        actual val porcupine: AudioFormatEncodingType = Default
        actual fun supportedValues(): List<AudioFormatEncodingType> {
            //TODO("Not yet implemented")
            return listOf()
        }

    }

}