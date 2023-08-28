package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

enum class VoiceActivityDetectionOption(override val text: StableStringResource) : IOption<VoiceActivityDetectionOption> {

    Enabled(MR.strings.enabled.stable),
    Disabled(MR.strings.disabled.stable);

    override fun findValue(value: String): VoiceActivityDetectionOption {
        return valueOf(value)
    }
}