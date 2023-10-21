package org.rhasspy.mobile.data.service.option

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

@Serializable
enum class WakeDomainOption(override val text: StableStringResource) : IOption {

    Porcupine(MR.strings.localPorcupine.stable),
    Rhasspy2HermesMQTT(MR.strings.rhasspy2hermes_mqtt.stable),
    Rhasspy3WyomingHttp(MR.strings.rhasspy3wyoming_http.stable),
    Rhasspy3WyomingWebsocket(MR.strings.rhasspy3wyoming_websocket.stable),
    Udp(MR.strings.udpAudioOutput.stable),
    Disabled(MR.strings.disabled.stable);

}