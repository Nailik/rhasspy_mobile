package org.rhasspy.mobile.logger

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.readOnly

class EventLogger constructor(val tag: EventTag) {

    private val _events = MutableStateFlow<List<Event>>(listOf())
    val events = _events.readOnly

    fun event(type: EventType): Event {
        val event = Event(type)
        _events.value = _events.value.toMutableList().also {
            it.add(event)
        }
        return event
    }

}