package org.rhasspy.mobile.data.resource

import androidx.compose.runtime.Stable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import org.rhasspy.mobile.data.resource.StableStringResource.StableResourceFormattedStringDesc
import org.rhasspy.mobile.data.resource.StableStringResource.StableStringResourceSingle

val StringResource.stable get() = StableStringResourceSingle(this)
val ResourceFormattedStringDesc.stable get() = StableResourceFormattedStringDesc(this)

sealed interface StableStringResource {

    @Stable
    data class StableStringResourceSingle(
        val stringResource: StringResource
    ) : StableStringResource

    @Stable
    data class StableResourceFormattedStringDesc(
        val stringResource: ResourceFormattedStringDesc
    ) : StableStringResource


}

