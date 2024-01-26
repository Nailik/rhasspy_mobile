package org.rhasspy.mobile.data.service.option

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import kotlin.enums.EnumEntries

@Stable
interface IOption {
    val text: StableStringResource
    val name: String
}