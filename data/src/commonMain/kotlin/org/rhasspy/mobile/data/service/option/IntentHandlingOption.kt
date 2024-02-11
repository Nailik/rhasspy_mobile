package org.rhasspy.mobile.data.service.option

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

@Serializable
enum class IntentHandlingOption(override val text: StableStringResource) : IOption {

    HomeAssistant(MR.strings.homeAssistant.stable),
    Rhasspy2HermesHttp(MR.strings.rhasspy2hermes_http.stable),
    WithRecognition(MR.strings.withRecognition.stable),
    Disabled(MR.strings.disabled.stable);

}