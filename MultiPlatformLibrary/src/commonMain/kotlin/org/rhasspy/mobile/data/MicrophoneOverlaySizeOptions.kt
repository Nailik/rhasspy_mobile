package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class MicrophoneOverlaySizeOptions(override val text: StringResource) : DataEnum<MicrophoneOverlaySizeOptions> {
    Small(MR.strings.small),
    Medium(MR.strings.medium),
    Big(MR.strings.big),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): MicrophoneOverlaySizeOptions {
        return valueOf(value)
    }
}