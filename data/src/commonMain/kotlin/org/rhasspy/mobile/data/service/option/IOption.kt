package org.rhasspy.mobile.data.service.option

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource

@Stable
interface IOption {
    val text: StableStringResource
    val name: String
}