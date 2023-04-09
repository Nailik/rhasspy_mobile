package org.rhasspy.mobile.data.resource

import androidx.compose.runtime.Stable
import dev.icerock.moko.resources.StringResource

val StringResource.stable get() = StableStringResource(this)

@Stable
data class StableStringResource(
    val stringResource: StringResource
)