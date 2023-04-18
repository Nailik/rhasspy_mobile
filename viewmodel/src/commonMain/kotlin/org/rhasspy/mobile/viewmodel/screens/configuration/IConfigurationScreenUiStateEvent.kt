package org.rhasspy.mobile.viewmodel.screens.configuration

import org.rhasspy.mobile.ui.event.Event
import org.rhasspy.mobile.ui.event.StateEvent

sealed class IConfigurationScreenUiStateEvent(stateEvent: StateEvent) : Event(stateEvent) {

    data class ScrollToErrorEventIState(
        override val stateEvent: StateEvent,
        val firstErrorIndex: Int
    ) : IConfigurationScreenUiStateEvent(stateEvent)

}