package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class IntentRecognitionOptions(override val text: StringResource) : DataEnum<IntentRecognitionOptions> {
    RemoteHTTP(MR.strings.remoteHTTP),
    RemoteMQTT(MR.strings.remoteMQTT),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): IntentRecognitionOptions {
        return valueOf(value)
    }
}