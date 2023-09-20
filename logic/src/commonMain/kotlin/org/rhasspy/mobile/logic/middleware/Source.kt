package org.rhasspy.mobile.logic.middleware

sealed interface Source {
    data object Local : Source
    data object HttpApi : Source
    data object Mqtt : Source

}