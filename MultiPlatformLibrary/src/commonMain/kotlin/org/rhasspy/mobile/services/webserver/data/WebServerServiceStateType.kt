package org.rhasspy.mobile.services.webserver.data

import org.rhasspy.mobile.services.state.StateType

enum class WebServerServiceStateType : StateType {
    STARTING,
    RECEIVING
}