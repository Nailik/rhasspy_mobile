package org.rhasspy.mobile.data.serviceoption

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class IntentRecognitionOption(override val text: StringResource) :
    IOption<IntentRecognitionOption> {
    RemoteHTTP(MR.strings.remoteHTTP),
    RemoteMQTT(MR.strings.remoteMQTT),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): IntentRecognitionOption {
        return valueOf(value)
    }
}