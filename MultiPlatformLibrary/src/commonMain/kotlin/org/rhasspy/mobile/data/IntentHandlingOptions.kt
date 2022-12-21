package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class IntentHandlingOptions(override val text: StringResource) :
    DataEnum<IntentHandlingOptions> {
    HomeAssistant(MR.strings.homeAssistant),
    RemoteHTTP(MR.strings.remoteHTTP),
    WithRecognition(MR.strings.withRecognition),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): IntentHandlingOptions {
        return valueOf(value)
    }
}