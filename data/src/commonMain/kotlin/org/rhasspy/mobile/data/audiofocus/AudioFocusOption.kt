package org.rhasspy.mobile.data.audiofocus

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

@Stable
enum class AudioFocusOption(override val text: StableStringResource) : IOption<AudioFocusOption> {

    Disabled(MR.strings.disabled.stable),
    PauseAndResume(MR.strings.pauseAndResume.stable),
    Duck(MR.strings.duck.stable);

    override val internalEntries get() = entries

}