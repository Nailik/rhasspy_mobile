package org.rhasspy.mobile.data.serviceoption

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class AudioOutputOption(override val text: StringResource) : IOption<AudioOutputOption> {
    Sound(MR.strings.sound),
    Notification(MR.strings.notification);

    override fun findValue(value: String): AudioOutputOption {
        return valueOf(value)
    }
}