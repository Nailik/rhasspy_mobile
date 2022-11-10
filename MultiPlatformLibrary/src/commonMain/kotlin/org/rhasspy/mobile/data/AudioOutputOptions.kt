package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class AudioOutputOptions(override val text: StringResource) : DataEnum<AudioOutputOptions> {
    Sound(MR.strings.sound),
    Notification(MR.strings.notification);

    override fun findValue(value: String): AudioOutputOptions {
        return valueOf(value)
    }
}