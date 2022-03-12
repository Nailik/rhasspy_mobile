package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource

interface DataEnum<T> {
    val text: StringResource
    val name: String

    fun findValue(value: String): T
}

