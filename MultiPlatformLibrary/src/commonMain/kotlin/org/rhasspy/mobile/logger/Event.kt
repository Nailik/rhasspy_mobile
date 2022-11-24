package org.rhasspy.mobile.logger

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.readOnly

data class Event(val type: EventType) {

    private val logger = Logger.withTag(type.toString())
    private val _eventState = MutableStateFlow<EventState>(EventState.Pending)
    val eventState = _eventState.readOnly

    init {
        logger.v { "init" }
    }

    fun pending() {
        logger.v { "pending" }
        _eventState.value = EventState.Pending
    }

    fun loading() {
        logger.v { "loading" }
        if (_eventState.value == EventState.Pending) {
            _eventState.value = EventState.Loading
        }
    }

    fun success(information: String? = null) {
        logger.v { information ?: "" }
        if (_eventState.value == EventState.Loading) {
            _eventState.value = EventState.Success(information)
        }
    }

    fun warning(information: String? = null) {
        logger.w { information ?: "" }
        if (_eventState.value == EventState.Loading) {
            _eventState.value = EventState.Warning(information)
        }
    }

    fun error(exception: Throwable) {
        logger.e(exception) { "" }
        if (_eventState.value == EventState.Loading || _eventState.value == EventState.Pending) {
            _eventState.value = EventState.Error(exception.message)
        }
    }

    fun error(exception: Exception) {
        logger.e(exception) { "" }
        if (_eventState.value == EventState.Loading || _eventState.value == EventState.Pending) {
            _eventState.value = EventState.Error(exception.message)
        }
    }

    fun error(exception: String) {
        logger.e { exception }
        if (_eventState.value == EventState.Loading || _eventState.value == EventState.Pending) {
            _eventState.value = EventState.Error(exception)
        }
    }

}