package org.rhasspy.mobile.services.webserver.data

import org.rhasspy.mobile.services.state.StateType

enum class WebServerLinkStateType : StateType {
    STARTING,
    RECEIVING
}