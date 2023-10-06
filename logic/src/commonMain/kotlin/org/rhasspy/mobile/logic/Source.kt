package org.rhasspy.mobile.logic

internal sealed interface Source {
    data object Local : Source
    data object HttpApi : Source
    data object Mqtt : Source

}