package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.data.resource.StableStringResource

interface IOption<T> {
    val text: StableStringResource
    val name: String

    fun findValue(value: String): T
}

