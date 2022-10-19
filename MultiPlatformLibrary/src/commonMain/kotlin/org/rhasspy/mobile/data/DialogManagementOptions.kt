package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class DialogManagementOptions(override val text: StringResource) : DataEnum<DialogManagementOptions> {
    Local(MR.strings.local),
    RemoteMQTT(MR.strings.remoteMQTT),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): DialogManagementOptions {
        return valueOf(value)
    }
}