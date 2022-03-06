package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class WakeWordOption(override val text: StringResource) : DataEnum {
    Porcupine(MR.strings.localPorcupine),
    MQTT(MR.strings.mqtt),
    Disabled(MR.strings.disabled)
}