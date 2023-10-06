package org.rhasspy.mobile.logic.connections.mqtt

internal sealed interface MqttResult {

    data object Success : MqttResult
    data object Error : MqttResult


}