package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

enum class MicrophoneOverlaySizeOption(override val text: StableStringResource, val size: Int) :
    IOption<MicrophoneOverlaySizeOption> {
    Small(MR.strings.small.stable, 64),
    Medium(MR.strings.medium.stable, 96),
    Big(MR.strings.big.stable, 128),
    Disabled(MR.strings.disabled.stable, 0);

    override fun findValue(value: String): MicrophoneOverlaySizeOption {
        return valueOf(value)
    }
}