package org.rhasspy.mobile.data.service.option

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

@Serializable
enum class HomeAssistantIntentHandlingOption(override val text: StableStringResource) : IOption {

    Event(MR.strings.hassEvent.stable),
    Intent(MR.strings.intentHandling.stable);

}