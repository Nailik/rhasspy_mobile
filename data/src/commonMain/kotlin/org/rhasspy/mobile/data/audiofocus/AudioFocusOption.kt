package org.rhasspy.mobile.data.audiofocus

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption

@Stable
enum class AudioFocusOption(override val text: StableStringResource) : IOption<AudioFocusOption> {

    Disabled(MR.strings.disabled.stable),
    PauseAndResume(MR.strings.pauseAndResume.stable),
    Duck(MR.strings.duck.stable);

    override fun findValue(value: String): AudioFocusOption {
        return AudioFocusOption.valueOf(value)
    }

}