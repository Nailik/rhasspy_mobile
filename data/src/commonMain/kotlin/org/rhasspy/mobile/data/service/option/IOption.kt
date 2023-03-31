package org.rhasspy.mobile.data.service.option

import dev.icerock.moko.resources.StringResource

interface IOption<T> {
    val text: StringResource
    val name: String

    fun findValue(value: String): T
}

