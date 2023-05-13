package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable

enum class TextToSpeechOption(override val text: StableStringResource) : IOption<TextToSpeechOption> {
    RemoteHTTP(MR.strings.remoteHTTP.stable),
    RemoteMQTT(MR.strings.remoteMQTT.stable),
    Disabled(MR.strings.disabled.stable);

    override fun findValue(value: String): TextToSpeechOption {
        return valueOf(value)
    }
}