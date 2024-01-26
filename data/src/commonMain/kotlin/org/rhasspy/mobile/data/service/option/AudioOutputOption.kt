package org.rhasspy.mobile.data.service.option

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

@Serializable
enum class AudioOutputOption(override val text: StableStringResource) : IOption {

    Sound(MR.strings.sound.stable),
    Notification(MR.strings.notification.stable);

}