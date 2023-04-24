package org.rhasspy.mobile.data.audiofocus

import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption

enum class AudioFocusType(override val text: StableStringResource) : IOption<AudioFocusType> {

    Disabled(MR.strings.en.stable),
    PauseAndResume(MR.strings.de.stable),
    Duck(MR.strings.de.stable);

    override fun findValue(value: String): AudioFocusType {
        return AudioFocusType.valueOf(value)
    }


}