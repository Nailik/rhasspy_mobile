package org.rhasspy.mobile.data.audiorecorder

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

@Serializable
actual enum class AudioSourceType(
    override val text: StableStringResource,
    actual val value: Int
) : IOption {

    Default(MR.strings.defaultText.stable, 1);

    actual companion object {
        actual val default: AudioSourceType get() = Default
        actual fun supportedValues() = listOf(Default)
    }

}