package org.rhasspy.mobile.services.mqtt

import com.benasher44.uuid.Uuid

data class MqttResultCallback(val uuid: Uuid, val resultTopic: String, val callback: suspend (mqttMessage: MqttMessage) -> Unit)