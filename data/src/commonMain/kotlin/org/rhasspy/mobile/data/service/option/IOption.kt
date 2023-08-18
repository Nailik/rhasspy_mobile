package org.rhasspy.mobile.data.service.option

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource

@Stable
interface IOption<T> {
    val text: StableStringResource
    val name: String

    fun findValue(value: String): IOption<T>
}

