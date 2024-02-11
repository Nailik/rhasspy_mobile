package org.rhasspy.mobile.data.service.option

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

@Serializable
enum class MicrophoneOverlaySizeOption(override val text: StableStringResource, val size: Int) : IOption {

    Small(MR.strings.small.stable, 64),
    Medium(MR.strings.medium.stable, 96),
    Big(MR.strings.big.stable, 128),
    Disabled(MR.strings.disabled.stable, 0);

}