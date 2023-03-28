package org.rhasspy.mobile.data.serviceoption

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class WakeWordOption(override val text: StringResource) : IOption<WakeWordOption> {
    Porcupine(MR.strings.localPorcupine),
    MQTT(MR.strings.mqtt),
    Udp(MR.strings.udpAudioOutput),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): WakeWordOption {
        return valueOf(value)
    }
}