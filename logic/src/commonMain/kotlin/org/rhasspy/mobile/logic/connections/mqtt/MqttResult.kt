package org.rhasspy.mobile.logic.connections.mqtt

sealed interface MqttResult {

    data object Success : MqttResult
    data object Error : MqttResult


}