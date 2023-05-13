package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable

enum class WakeWordOption(override val text: StableStringResource) : IOption<WakeWordOption> {
    Porcupine(MR.strings.localPorcupine.stable),
    MQTT(MR.strings.mqtt.stable),
    Udp(MR.strings.udpAudioOutput.stable),
    Disabled(MR.strings.disabled.stable);

    override fun findValue(value: String): WakeWordOption {
        return valueOf(value)
    }
}