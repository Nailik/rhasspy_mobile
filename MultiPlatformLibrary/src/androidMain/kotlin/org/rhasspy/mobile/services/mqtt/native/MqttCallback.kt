package org.rhasspy.mobile.services.mqtt.native

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.rhasspy.mobile.services.mqtt.MqttMessage
import org.rhasspy.mobile.services.mqtt.MqttQos.Companion.createMqttQos
import org.eclipse.paho.client.mqttv3.MqttCallback as PahoMqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage as PahoMqttMessage


@ExperimentalStdlibApi
/** Handles all MQTT callbacks. */
internal class MqttCallback(
    var deliveryCompleteHandler: (Int) -> Unit,
    var connectionLostHandler: (String) -> Unit,
    var messageArrivedHandler: (String, MqttMessage) -> Unit
) : PahoMqttCallback {
    override fun messageArrived(topic: String, msg: PahoMqttMessage) {
        messageArrivedHandler(topic, createMqttMessage(msg))
    }

    override fun connectionLost(cause: Throwable) {
        connectionLostHandler(cause.message ?: "")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
        deliveryCompleteHandler(token.messageId)
    }

    private fun createMqttMessage(pahoMsg: PahoMqttMessage) = MqttMessage(
        msgId = pahoMsg.id,
        qos = createMqttQos(pahoMsg.qos),
        payload = pahoMsg.payload.decodeToString(),
        retained = pahoMsg.isRetained,
        duplicate = pahoMsg.isDuplicate
    )
}
