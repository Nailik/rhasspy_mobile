package org.rhasspy.mobile.android.stability

import androidx.compose.runtime.Stable
import dev.icerock.moko.resources.StringResource

fun StringResource.stable() : StableStringResource{
    return StableStringResource(this)
}

@Stable
data class StableStringResource(
    val stringResource: StringResource
)