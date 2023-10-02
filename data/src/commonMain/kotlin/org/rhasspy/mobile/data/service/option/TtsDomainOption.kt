package org.rhasspy.mobile.data.service.option

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

@Serializable
enum class TtsDomainOption(override val text: StableStringResource) : IOption {

    Rhasspy2HermesHttp(MR.strings.rhasspy2hermes_http.stable),
    Rhasspy2HermesMQTT(MR.strings.rhasspy2hermes_mqtt.stable),
    Disabled(MR.strings.disabled.stable);

}