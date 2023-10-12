package org.rhasspy.mobile.data.sounds

import dev.icerock.moko.resources.FileResource
import kotlinx.serialization.Serializable
import org.rhasspy.mobile.resources.MR

@Serializable
enum class IndicationSoundType(val default: FileResource) {

    Error(MR.files.etc_wav_beep_error),
    Wake(MR.files.etc_wav_beep_hi),
    Recorded(MR.files.etc_wav_beep_lo);

}