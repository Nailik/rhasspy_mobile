package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class TextToSpeechOptions(override val text: StringResource) : DataEnum<TextToSpeechOptions> {
    RemoteHTTP(MR.strings.remoteHTTP),
    RemoteMQTT(MR.strings.remoteMQTT),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): TextToSpeechOptions {
        return valueOf(value)
    }
}