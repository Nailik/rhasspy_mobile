package org.rhasspy.mobile.data.theme

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

@Serializable
enum class ThemeType(override val text: StableStringResource) : IOption {

    System(MR.strings.system.stable),
    Light(MR.strings.light.stable),
    Dark(MR.strings.dark.stable);

}