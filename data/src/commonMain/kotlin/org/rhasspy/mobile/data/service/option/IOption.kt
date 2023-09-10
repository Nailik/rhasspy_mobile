package org.rhasspy.mobile.data.service.option

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource
import kotlin.enums.EnumEntries

@Stable
interface IOption<T : Enum<T>> {
    val text: StableStringResource
    val name: String

    val internalEntries: EnumEntries<T>

    fun findValue(value: String): T? = internalEntries.firstOrNull { it.name == name }

}

