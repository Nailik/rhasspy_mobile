package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class SpeechToTextOptions(override val text: StringResource) : DataEnum<SpeechToTextOptions> {
    RemoteHTTP(MR.strings.remoteHTTP),
    RemoteMQTT(MR.strings.remoteMQTT),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): SpeechToTextOptions {
        return valueOf(value)
    }
}