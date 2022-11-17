package org.rhasspy.mobile.services.webserver

import org.rhasspy.mobile.services.state.StateType

enum class WebServerServiceStateType : StateType {
    STARTING,
    RECEIVING
}