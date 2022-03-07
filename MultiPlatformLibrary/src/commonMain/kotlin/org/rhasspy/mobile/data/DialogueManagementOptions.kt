package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class DialogueManagementOptions(override val text: StringResource) : DataEnum<DialogueManagementOptions> {
    Local(MR.strings.local),
    RemoteMQTT(MR.strings.remoteMQTT),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): DialogueManagementOptions {
        return valueOf(value)
    }
}