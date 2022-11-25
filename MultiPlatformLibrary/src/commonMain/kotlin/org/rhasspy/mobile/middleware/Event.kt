package org.rhasspy.mobile.middleware

import kotlinx.coroutines.flow.MutableStateFlow

class Event(val eventType: EventType, description: String? = null) {

    val eventState = MutableStateFlow<EventState>(EventState.Loading)

    //initially loading
    fun loading(): Event {
        return this
    }

    fun success() {
        //when not error/warning
    }

    fun warning() {
        //when not error
    }

    fun warning(errorType: ErrorType) {
        //when not error
    }

    fun error() {

    }

    fun error(errorType: ErrorType) {

    }

    fun error(exception: Throwable) {

    }

    fun error(exception: Exception) {

    }

    fun error(exception: String) {

    }

}