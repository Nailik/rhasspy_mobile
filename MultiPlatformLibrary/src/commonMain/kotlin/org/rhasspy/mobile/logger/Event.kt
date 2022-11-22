package org.rhasspy.mobile.logger

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.readOnly

data class Event(val type: EventType) {

    private val _eventState = MutableStateFlow<EventState>(EventState.Pending)
    val eventState = _eventState.readOnly

    fun pending() {
        _eventState.value = EventState.Pending
    }

    fun loading() {
        if (_eventState.value == EventState.Pending) {
            _eventState.value = EventState.Loading
        }
    }

    fun success(information: String? = null) {
        if (_eventState.value == EventState.Loading) {
            _eventState.value = EventState.Success(information)
        }
    }

    fun warning(information: String? = null) {
        if (_eventState.value == EventState.Loading) {
            _eventState.value = EventState.Warning(information)
        }
    }

    fun error(exception: Exception) {
        if (_eventState.value == EventState.Loading) {
            _eventState.value = EventState.Error(exception.message)
        }
    }

    fun error(exception: String) {
        if (_eventState.value == EventState.Loading) {
            _eventState.value = EventState.Error(exception)
        }
    }

}