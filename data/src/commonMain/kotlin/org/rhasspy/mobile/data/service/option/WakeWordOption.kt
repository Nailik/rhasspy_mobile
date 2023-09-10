package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

enum class WakeWordOption(override val text: StableStringResource) : IOption<WakeWordOption> {

    Porcupine(MR.strings.localPorcupine.stable),
    Rhasspy2HermesMQTT(MR.strings.rhasspy2hermes_mqtt.stable),
    Udp(MR.strings.udpAudioOutput.stable),
    Disabled(MR.strings.disabled.stable);

    override val internalEntries = entries

}