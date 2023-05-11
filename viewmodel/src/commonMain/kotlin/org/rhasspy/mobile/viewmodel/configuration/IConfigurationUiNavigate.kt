package org.rhasspy.mobile.viewmodel.configuration

import org.rhasspy.mobile.data.event.Event
import org.rhasspy.mobile.data.event.EventState

sealed class IConfigurationUiNavigate(eventState: EventState) : Event(eventState) {

    data class PopBackStack(override val eventState: EventState) : IConfigurationUiNavigate(eventState)

}