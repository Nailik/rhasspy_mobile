package org.rhasspy.mobile.viewmodel

fun getRandomString(length: Int): String {
    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..length)
        .map { charset.random() }
        .joinToString("")
}

/**
 * return random enum value of enum class T
 */
inline fun <reified T : Enum<T>> randomEnum(): T {
    val enumValues: Array<T> = enumValues()
    return enumValues[(enumValues.indices).random()]
}