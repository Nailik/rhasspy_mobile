package org.rhasspy.mobile.data.audiorecorder

import android.annotation.SuppressLint
import android.media.AudioFormat.*
import android.os.Build
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption

actual enum class AudioRecorderEncodingType(
    override val text: StableStringResource,
    actual val value: Int,
    actual val bitRate: Int
) : IOption<AudioRecorderEncodingType> {

    Default(MR.strings.encoding_type_default.stable, ENCODING_DEFAULT, 2),
    PCM8Bit(MR.strings.encoding_type_PCM8Bit.stable, ENCODING_PCM_8BIT, 1),
    PCM16Bit(MR.strings.encoding_type_PCM16Bit.stable, ENCODING_PCM_16BIT, 2),
    PCM_FLOAT(MR.strings.encoding_type_PCM_FLOAT.stable, ENCODING_PCM_FLOAT, 4),

    @SuppressLint("InlinedApi")
    IEC61937(MR.strings.encoding_type_IEC61937.stable, ENCODING_IEC61937, 2),

    @SuppressLint("InlinedApi")
    PCM24BITPacked(MR.strings.encoding_type_PCM24BITPacked.stable, ENCODING_PCM_24BIT_PACKED, 3),

    @SuppressLint("InlinedApi")
    PCM_32BIT(MR.strings.encoding_type_PCM_32BIT.stable, ENCODING_PCM_32BIT, 4);

    override fun findValue(value: String): AudioRecorderEncodingType {
        return AudioRecorderEncodingType.valueOf(value)
    }

    actual companion object {
        actual val default: AudioRecorderEncodingType get() = Default

        actual fun supportedValues(): List<AudioRecorderEncodingType> {
            return mutableListOf(
                Default,
                PCM8Bit,
                PCM16Bit,
                PCM_FLOAT,
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    add(IEC61937)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    add(PCM24BITPacked)
                    add(PCM_32BIT)
                }
            }
        }
    }
}