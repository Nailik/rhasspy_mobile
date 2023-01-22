package org.rhasspy.mobile.middleware

sealed class Source {
    object Local : Source()
    object HttpApi : Source()
    class Mqtt(val sessionId: String?) : Source()

    override fun toString(): String {
        return this::class.simpleName ?: super.toString()
    }
}