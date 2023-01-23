package org.rhasspy.mobile.logic.settings.option

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class AudioPlayingOption(override val text: StringResource) : IOption<AudioPlayingOption> {
    Local(MR.strings.local),
    RemoteHTTP(MR.strings.remoteHTTP),
    RemoteMQTT(MR.strings.remoteMQTT),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): AudioPlayingOption {
        return valueOf(value)
    }
}