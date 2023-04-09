package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable

enum class IntentRecognitionOption(override val text: StableStringResource) :
    IOption<IntentRecognitionOption> {
    RemoteHTTP(MR.strings.remoteHTTP.stable),
    RemoteMQTT(MR.strings.remoteMQTT.stable),
    Disabled(MR.strings.disabled.stable);

    override fun findValue(value: String): IntentRecognitionOption {
        return valueOf(value)
    }
}