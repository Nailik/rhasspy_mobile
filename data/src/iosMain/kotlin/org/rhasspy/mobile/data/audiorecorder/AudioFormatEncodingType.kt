package org.rhasspy.mobile.data.audiorecorder

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

@Serializable
actual enum class AudioFormatEncodingType(
    override val text: StableStringResource,
    actual val value: Int,
    actual val bitRate: Int
) : IOption {

    //TODO #509
    Default(MR.strings.defaultText.stable, 1, 1);

    actual companion object {
        actual val default: AudioFormatEncodingType = Default
        actual fun supportedValues(): List<AudioFormatEncodingType> {
            //TODO #509
            return listOf()
        }

    }

}