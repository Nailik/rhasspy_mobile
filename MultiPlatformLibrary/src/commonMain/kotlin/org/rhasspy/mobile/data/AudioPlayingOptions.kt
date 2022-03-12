package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class AudioPlayingOptions(override val text: StringResource) : DataEnum<AudioPlayingOptions> {
    Local(MR.strings.local),
    RemoteHTTP(MR.strings.remoteHTTP),
    RemoteMQTT(MR.strings.remoteMQTT),
    Disabled(MR.strings.disabled);

    override fun findValue(value: String): AudioPlayingOptions {
        return valueOf(value)
    }
}