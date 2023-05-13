package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable

enum class HomeAssistantIntentHandlingOption(override val text: StableStringResource) : IOption<HomeAssistantIntentHandlingOption> {
    Event(MR.strings.hassEvent.stable),
    Intent(MR.strings.intentHandling.stable);

    override fun findValue(value: String): HomeAssistantIntentHandlingOption {
        return HomeAssistantIntentHandlingOption.valueOf(value)
    }
}