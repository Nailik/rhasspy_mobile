package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

enum class AudioOutputOption(override val text: StableStringResource) : IOption<AudioOutputOption> {

    Sound(MR.strings.sound.stable),
    Notification(MR.strings.notification.stable);

    override val internalEntries get() = entries

}