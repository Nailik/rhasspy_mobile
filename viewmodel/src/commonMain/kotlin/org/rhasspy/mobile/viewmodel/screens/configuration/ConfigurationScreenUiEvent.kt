package org.rhasspy.mobile.viewmodel.screens.configuration

import org.rhasspy.mobile.ui.event.Event
import org.rhasspy.mobile.ui.event.StateEvent

sealed class ConfigurationScreenUiEvent(stateEvent: StateEvent) : Event(stateEvent) {

    data class ScrollToErrorEvent(
        override val stateEvent: StateEvent,
        val firstErrorIndex: Int
    ) : ConfigurationScreenUiEvent(stateEvent)

}