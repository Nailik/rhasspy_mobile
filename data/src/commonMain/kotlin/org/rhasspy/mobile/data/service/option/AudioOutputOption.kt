package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable

enum class AudioOutputOption(override val text: StableStringResource) : IOption<AudioOutputOption> {
    Sound(MR.strings.sound.stable),
    Notification(MR.strings.notification.stable);

    override fun findValue(value: String): AudioOutputOption {
        return valueOf(value)
    }
}