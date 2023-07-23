package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

enum class AudioPlayingOption(override val text: StableStringResource) :
    IOption<AudioPlayingOption> {
    Local(MR.strings.local.stable),
    RemoteHTTP(MR.strings.remoteHTTP.stable),
    RemoteMQTT(MR.strings.remoteMQTT.stable),
    Disabled(MR.strings.disabled.stable);

    override fun findValue(value: String): AudioPlayingOption {
        return valueOf(value)
    }
}