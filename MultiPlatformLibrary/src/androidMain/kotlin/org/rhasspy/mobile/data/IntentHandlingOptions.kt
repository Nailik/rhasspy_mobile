package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class IntentHandlingOptions(override val text: StringResource) : DataEnum {
    HomeAssistant(MR.strings.homeAssistant),
    RemoteMQTT(MR.strings.remoteMQTT),
    Disabled(MR.strings.disabled)
}