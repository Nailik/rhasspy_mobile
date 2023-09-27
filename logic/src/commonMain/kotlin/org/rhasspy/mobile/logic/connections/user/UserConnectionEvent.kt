package org.rhasspy.mobile.logic.connections.user

sealed interface UserConnectionEvent {

    data object StartStopRhasspy: UserConnectionEvent
    data object StartStopPlay: UserConnectionEvent

}