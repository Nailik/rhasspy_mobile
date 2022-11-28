package org.rhasspy.mobile.middleware

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.readOnly

class Event(val eventType: EventType, val description: String? = null) {

    private val _eventState = MutableStateFlow<EventState>(EventState.Loading)
    val eventState = _eventState.readOnly

    //initially loading
    fun loading(): Event {
        return this
    }

    fun success() {
        if (_eventState.value == EventState.Loading) {
            _eventState.value = EventState.Success()
            //when not error/warning
        }
    }
    fun success(data: String) {
        if (_eventState.value == EventState.Loading) {
            _eventState.value = EventState.Success(data)
            //when not error/warning
        }
    }

    fun warning() {
        if (_eventState.value == EventState.Loading) {
            //when not error
            _eventState.value = EventState.Warning()
        }
    }

    fun warning(errorType: ErrorType) {
        if (_eventState.value == EventState.Loading) {
            //when not error
            _eventState.value = EventState.Warning()
        }
    }

    fun error() {
        if (_eventState.value == EventState.Loading) {

            _eventState.value = EventState.Error()
        }
    }

    fun error(errorType: ErrorType) {
        if (_eventState.value == EventState.Loading) {

            _eventState.value = EventState.Error(errorType)
        }
    }

    fun error(exception: Throwable) {
        if (_eventState.value == EventState.Loading) {

            _eventState.value = EventState.Error()
        }
    }

    fun error(exception: Exception) {
        if (_eventState.value == EventState.Loading) {

            _eventState.value = EventState.Error()
        }
    }

    fun error(exception: String) {
        if (_eventState.value == EventState.Loading) {

            _eventState.value = EventState.Error()
        }
    }

}