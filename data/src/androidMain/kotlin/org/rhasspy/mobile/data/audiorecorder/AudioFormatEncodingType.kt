package org.rhasspy.mobile.data.audiorecorder

import android.annotation.SuppressLint
import android.media.AudioFormat.*
import android.os.Build
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

    PCM8Bit(MR.strings.encoding_type_PCM8Bit.stable, ENCODING_PCM_8BIT, 8),
    PCM16Bit(MR.strings.encoding_type_PCM16Bit.stable, ENCODING_PCM_16BIT, 16),

    @SuppressLint("InlinedApi")
    PCM_32BIT(MR.strings.encoding_type_PCM_32BIT.stable, ENCODING_PCM_32BIT, 32);

    actual companion object {
        actual val default: AudioFormatEncodingType get() = PCM16Bit

        actual val porcupine: AudioFormatEncodingType get() = PCM16Bit

        actual fun supportedValues(): List<AudioFormatEncodingType> {
            return mutableListOf(
                PCM8Bit,
                PCM16Bit,
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    add(PCM_32BIT)
                }
            }
        }

    }
}