package org.rhasspy.mobile.settings.option

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class TextToSpeechOption(override val text: StringResource) : IOption<TextToSpeechOption> {
    RemoteHTTP(MR.strings.remoteHTTP),
    RemoteMQTT(MR.strings.remoteMQTT),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): TextToSpeechOption {
        return valueOf(value)
    }
}