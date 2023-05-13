package org.rhasspy.mobile.data.audiorecorder

import android.annotation.SuppressLint
import android.media.AudioFormat.*
import android.os.Build
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

actual enum class AudioRecorderEncodingType(
    override val text: StableStringResource,
    actual val value: Int,
    actual val bitRate: Int
) : IOption<AudioRecorderEncodingType> {

    Default(MR.strings.encoding_type_default.stable, ENCODING_DEFAULT, 8),
    PCM8Bit(MR.strings.encoding_type_PCM8Bit.stable, ENCODING_PCM_8BIT, 8),
    PCM16Bit(MR.strings.encoding_type_PCM16Bit.stable, ENCODING_PCM_16BIT, 16),

    @SuppressLint("InlinedApi")
    PCM_32BIT(MR.strings.encoding_type_PCM_32BIT.stable, ENCODING_PCM_32BIT, 32);

    override fun findValue(value: String): AudioRecorderEncodingType {
        return AudioRecorderEncodingType.valueOf(value)
    }

    actual companion object {
        actual val default: AudioRecorderEncodingType get() = PCM16Bit

        actual fun supportedValues(): List<AudioRecorderEncodingType> {
            return mutableListOf(
                Default,
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