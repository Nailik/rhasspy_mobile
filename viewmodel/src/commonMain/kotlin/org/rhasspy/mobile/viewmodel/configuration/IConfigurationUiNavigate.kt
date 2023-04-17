package org.rhasspy.mobile.viewmodel.configuration

import org.rhasspy.mobile.ui.event.Event
import org.rhasspy.mobile.ui.event.StateEvent

sealed class IConfigurationUiNavigate(stateEvent: StateEvent) : Event(stateEvent) {

    data class PopBackStack(override val stateEvent: StateEvent) : IConfigurationUiNavigate(stateEvent)

}