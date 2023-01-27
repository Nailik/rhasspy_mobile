package org.rhasspy.mobile.logic.settings.option

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class MicrophoneOverlaySizeOption(override val text: StringResource, val size: Int) :
    IOption<MicrophoneOverlaySizeOption> {
    Small(MR.strings.small, 64),
    Medium(MR.strings.medium, 96),
    Big(MR.strings.big, 128),
    Disabled(MR.strings.disabled, 0);

    override fun findValue(value: String): MicrophoneOverlaySizeOption {
        return valueOf(value)
    }
}