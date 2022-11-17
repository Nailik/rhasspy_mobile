package org.rhasspy.mobile.services.state

data class ServiceState(
    val state: State,
    val stateType: StateType,
    val description: Any? = null
)