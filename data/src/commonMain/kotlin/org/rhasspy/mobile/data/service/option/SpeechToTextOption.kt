package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

enum class SpeechToTextOption(override val text: StableStringResource) : IOption<SpeechToTextOption> {

    Rhasspy2HermesHttp(MR.strings.rhasspy2hermes_http.stable),
    Rhasspy2HermesMQTT(MR.strings.rhasspy2hermes_mqtt.stable),
    Disabled(MR.strings.disabled.stable);

    override val internalEntries = entries

}