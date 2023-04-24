package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable

enum class IntentHandlingOption(override val text: StableStringResource) :
    IOption<IntentHandlingOption> {
    HomeAssistant(MR.strings.homeAssistant.stable),
    RemoteHTTP(MR.strings.remoteHTTP.stable),
    WithRecognition(MR.strings.withRecognition.stable),
    Disabled(MR.strings.disabled.stable);

    override fun findValue(value: String): IntentHandlingOption {
        return valueOf(value)
    }
}