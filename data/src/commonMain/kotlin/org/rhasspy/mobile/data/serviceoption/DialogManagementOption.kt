package org.rhasspy.mobile.data.serviceoption

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class DialogManagementOption(override val text: StringResource) :
    IOption<DialogManagementOption> {
    Local(MR.strings.local),
    RemoteMQTT(MR.strings.remoteMQTT),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): DialogManagementOption {
        return valueOf(value)
    }
}