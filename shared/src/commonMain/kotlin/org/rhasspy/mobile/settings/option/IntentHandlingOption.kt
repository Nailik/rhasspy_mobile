package org.rhasspy.mobile.settings.option

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class IntentHandlingOption(override val text: StringResource) :
    IOption<IntentHandlingOption> {
    HomeAssistant(MR.strings.homeAssistant),
    RemoteHTTP(MR.strings.remoteHTTP),
    WithRecognition(MR.strings.withRecognition),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): IntentHandlingOption {
        return valueOf(value)
    }
}