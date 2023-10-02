package org.rhasspy.mobile.data.service.option

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

@Serializable
enum class HandleDomainOption(override val text: StableStringResource) : IOption {

    HomeAssistant(MR.strings.homeAssistant.stable),
    Disabled(MR.strings.disabled.stable);

}