package org.rhasspy.mobile.data.audiofocus

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

@Serializable
enum class AudioFocusOption(override val text: StableStringResource) : IOption {

    Disabled(MR.strings.disabled.stable),
    PauseAndResume(MR.strings.pauseAndResume.stable),
    Duck(MR.strings.duck.stable);

}