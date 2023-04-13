package org.rhasspy.mobile.ui.event

import androidx.compose.runtime.Stable

@Stable
abstract class Event(open val stateEvent: StateEvent)

/**
 *  This [StateEvent] can only have two primitive states.
 */
enum class StateEvent {
    /**
     *  The event is currently in its triggered state
     */
    Triggered,

    /**
     *  The event is currently in its consumed state
     */
    Consumed
}