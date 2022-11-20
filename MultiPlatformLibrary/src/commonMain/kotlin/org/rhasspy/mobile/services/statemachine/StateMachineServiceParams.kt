package org.rhasspy.mobile.services.statemachine

data class StateMachineServiceParams(
    val test: String
) {

    companion object {
        fun loadFromConfig() : StateMachineServiceParams {
            return StateMachineServiceParams("bar")
        }
    }

}