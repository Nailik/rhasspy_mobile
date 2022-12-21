package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class MicrophoneOverlaySizeOptions(override val text: StringResource, val size: Int) : DataEnum<MicrophoneOverlaySizeOptions> {
    Small(MR.strings.small, 64),
    Medium(MR.strings.medium, 96),
    Big(MR.strings.big, 128),
    Disabled(MR.strings.disabled, 0);

    override fun findValue(value: String): MicrophoneOverlaySizeOptions {
        return valueOf(value)
    }
}