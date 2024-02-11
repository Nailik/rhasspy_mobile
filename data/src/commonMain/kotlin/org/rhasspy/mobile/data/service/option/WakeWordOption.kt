package org.rhasspy.mobile.data.service.option

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

@Serializable
enum class WakeWordOption(override val text: StableStringResource) : IOption {

    Porcupine(MR.strings.localPorcupine.stable),
    Rhasspy2HermesMQTT(MR.strings.rhasspy2hermes_mqtt.stable),
    Udp(MR.strings.udpAudioOutput.stable),
    Disabled(MR.strings.disabled.stable);

}