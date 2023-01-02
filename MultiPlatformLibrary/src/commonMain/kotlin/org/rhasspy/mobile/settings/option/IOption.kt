package org.rhasspy.mobile.settings.option

import dev.icerock.moko.resources.StringResource

interface IOption<T> {
    val text: StringResource
    val name: String

    fun findValue(value: String): T
}

