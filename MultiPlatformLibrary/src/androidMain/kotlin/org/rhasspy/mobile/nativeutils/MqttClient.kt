package org.rhasspy.mobile.nativeutils

import co.touchlab.kermit.Logger
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import org.rhasspy.mobile.mqtt.*
import org.rhasspy.mobile.mqtt.MqttMessage
import org.rhasspy.mobile.services.mqtt.MqttServiceConnectionOptions
import org.rhasspy.mobile.settings.AppSettings

actual class MqttClient actual constructor(
    brokerUrl: String,
    clientId: String,
    persistenceType: MqttPersistence,
    onDelivered: (token: Int) -> Unit,
    onMessageReceived: (topic: String, message: MqttMessage) -> Unit,
    onDisconnect: (error: Throwable) -> Unit
) {
    private val logger = Logger.withTag("MqttClient")

    private var client = when (persistenceType) {
        MqttPersistence.MEMORY -> MqttClient(brokerUrl, clientId, MemoryPersistence())
        MqttPersistence.FILE -> MqttClient(brokerUrl, clientId, MqttDefaultFilePersistence())
        else -> MqttClient(brokerUrl, clientId)
    }

    private val callback = object : org.eclipse.paho.client.mqttv3.MqttCallback {
        override fun deliveryComplete(token: IMqttDeliveryToken) {
            onDelivered(token.messageId)
        }

        override fun messageArrived(topic: String, message: org.eclipse.paho.client.mqttv3.MqttMessage) {
            onMessageReceived(topic, message.toMqttMessage())
        }

        override fun connectionLost(error: Throwable) {
            onDisconnect(error)
            client.setCallback(null)
        }
    }

    actual val isConnected: Boolean
        get() = client.isConnected


    actual suspend fun publish(topic: String, msg: MqttMessage, timeout: Long): MqttError? = try {
        if (!topic.contains("audioFrame") || AppSettings.isLogAudioFramesEnabled.value) {
            //logging every audio frame really fills up the logs
            logger.v {
                "publish $topic $msg"
            }
        }

        client.publish(topic, org.eclipse.paho.client.mqttv3.MqttMessage(msg.payload).apply {
            id = msg.msgId
            qos = msg.qos.value
            isRetained = msg.retained
        })

        null
    } catch (mqttPersistenceEx: MqttPersistenceException) {
        MqttError(mqttPersistenceEx.message ?: "Message persistence failed.", MqttStatus.MSG_PERSISTENCE_FAILED)
    } catch (mqttEx: MqttException) {
        MqttError(mqttEx.message ?: "Message delivery failed.", MqttStatus.MSG_DELIVERY_FAILED)
    }

    /**
     * Subscribes to a topic.
     * @param topic The MQTT topic to use.
     * @param qos The MQTT quality of service to use.
     * @return Will return a [error][MqttError] if a problem has occurred. If a [error][MqttError] is returned then the
     * [subscribe failed][MqttStatus.SUBSCRIBE_FAILED] status (via [MqttError.statusCode]) is used.
     */
    actual suspend fun subscribe(topic: String, qos: MqttQos): MqttError? = try {
        logger.v { "subscribe $topic" }
        client.subscribe(topic, qos.value)
        null
    } catch (ex: MqttException) {
        MqttError(ex.message ?: "Subscription failed.", MqttStatus.SUBSCRIBE_FAILED)
    }

    @Suppress("unused")
    /**
     * Will unsubscribe from one or more topics.
     * @param topics One or more topics to unsubscribe from. Can include topic filter(s).
     * @return Will return a error if a problem has occurred. If a [error][MqttError] is returned then the
     * [unsubscribe failed][MqttStatus.UNSUBSCRIBE_FAILED] status (via [MqttError.statusCode]) is used.
     */
    actual suspend fun unsubscribe(vararg topics: String): MqttError? = try {
        logger.v { "unsubscribe" }
        client.unsubscribe(topics)
        null
    } catch (ex: MqttException) {
        MqttError(ex.message ?: "", MqttStatus.UNSUBSCRIBE_FAILED)
    }

    /**
     * Connects to the MQTT Broker.
     * @param connOptions The connection options to use.
     * @return Will return a [error][MqttError] if a problem has occurred. If a [error][MqttError] is returned then one
     * of the following [status objects][MqttStatus] (via [MqttError.statusCode]) is included:
     * - [Already connected][MqttStatus.ALREADY_CONNECTED]
     * - [Invalid credentials][MqttStatus.INVALID_CREDENTIALS]
     * - [Not authorized][MqttStatus.NOT_AUTHORIZED]
     * - [Server unavailable][MqttStatus.SERVER_UNAVAILABLE]
     * - [Identifier rejected][MqttStatus.IDENTIFIER_REJECTED]
     * - [Unacceptable protocol][MqttStatus.UNACCEPTABLE_PROTOCOL]
     */
    actual suspend fun connect(connOptions: MqttServiceConnectionOptions): MqttError? {
        logger.v { "connect" }

        return if (!isConnected) {
            connectToBroker(connOptions.toPhaoConnectOptions())
        } else {
            MqttError("Cannot connect to MQTT Broker.", MqttStatus.ALREADY_CONNECTED)
        }
    }

    /** Makes a attempt to establish a connection to the MQTT broker. */
    private fun connectToBroker(connOptions: MqttConnectOptions): MqttError? {
        logger.v { "connectToBroker" }
        var result: MqttError? = null
        var status = MqttStatus.SUCCESS
        try {
            client.connectWithResult(connOptions)
            client.setCallback(callback)
        } catch (securityEx: MqttSecurityException) {
            if (securityEx.reasonCode == 4) status = MqttStatus.INVALID_CREDENTIALS
            else if (securityEx.reasonCode == 5) status = MqttStatus.NOT_AUTHORIZED
        } catch (mqttEx: MqttException) {
            status = when (mqttEx.reasonCode) {
                3 -> MqttStatus.SERVER_UNAVAILABLE
                2 -> MqttStatus.IDENTIFIER_REJECTED
                1 -> MqttStatus.UNACCEPTABLE_PROTOCOL
                else -> MqttStatus.UNKNOWN
            }
        } catch (e: Exception) {
            //some exception occurred
            status = MqttStatus.UNKNOWN
        }
        if (status != MqttStatus.SUCCESS) {
            result = MqttError("Cannot connect to MQTT Broker.", status)
        }
        return result
    }

    /**
     * Disconnects from the MQTT Broker.
     * @return Will return a [error][MqttError] if a problem has occurred. If a [error][MqttError] is returned then the
     * [message persistence failed][MqttStatus.MSG_PERSISTENCE_FAILED] status (via [MqttError.statusCode]) is used.
     */
    actual fun disconnect(): MqttError? {
        logger.v { "disconnect" }
        var result: MqttError? = null
        if (client.isConnected) {
            try {
                client.disconnect()
                client.setCallback(null)
            } catch (ex: MqttException) {
                result = MqttError("Cannot disconnect from MQTT Broker.", MqttStatus.MSG_PERSISTENCE_FAILED)
            }
        }
        return result
    }

    private fun MqttServiceConnectionOptions.toPhaoConnectOptions(): MqttConnectOptions {
        return MqttConnectOptions().also {
            it.isCleanSession = cleanSession
            it.keepAliveInterval = keepAliveInterval
            it.userName = connUsername.ifEmpty { null }
            it.password = connPassword.toCharArray()
            it.connectionTimeout = connectionTimeout
        }
    }

    private fun org.eclipse.paho.client.mqttv3.MqttMessage.toMqttMessage(): MqttMessage {
        return MqttMessage(
            msgId = id,
            qos = MqttQos.createMqttQos(qos),
            payload = payload,
            retained = isRetained
        )
    }
}