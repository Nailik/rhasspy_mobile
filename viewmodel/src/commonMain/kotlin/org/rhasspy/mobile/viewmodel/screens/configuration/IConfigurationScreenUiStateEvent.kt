package org.rhasspy.mobile.viewmodel.screens.configuration

import org.rhasspy.mobile.ui.event.Event
import org.rhasspy.mobile.ui.event.EventState

sealed class IConfigurationScreenUiStateEvent(eventState: EventState) : Event(eventState) {

    data class ScrollToErrorEventIState(
        override val eventState: EventState,
        val firstErrorIndex: Int
    ) : IConfigurationScreenUiStateEvent(eventState)

}