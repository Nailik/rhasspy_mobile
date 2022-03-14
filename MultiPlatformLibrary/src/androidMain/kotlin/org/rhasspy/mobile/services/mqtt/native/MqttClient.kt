package org.rhasspy.mobile.services.mqtt.native

import co.touchlab.kermit.Logger
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import org.rhasspy.mobile.services.mqtt.*
import org.eclipse.paho.client.mqttv3.MqttClient as PahoMqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions as PahoConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage as PahoMqttMessage


actual class MqttClient actual constructor(
    brokerUrl: String,
    clientId: String,
    persistenceType: MqttPersistence,
    onDelivered: (token: Int) -> Unit,
    onMessageReceived: (topic: String, message: MqttMessage) -> Unit,
    onDisconnect: (error: Throwable) -> Unit
) {
    private val logger = Logger.withTag(this::class.simpleName!!)

    private var client = when (persistenceType) {
        MqttPersistence.MEMORY -> PahoMqttClient(brokerUrl, clientId, MemoryPersistence())
        MqttPersistence.FILE -> PahoMqttClient(brokerUrl, clientId, MqttDefaultFilePersistence())
        else -> PahoMqttClient(brokerUrl, clientId)
    }

    private val callback = object : MqttCallback {
        override fun deliveryComplete(token: IMqttDeliveryToken) {
            onDelivered(token.messageId)
        }

        override fun messageArrived(topic: String, message: PahoMqttMessage) {
            onMessageReceived(topic, message.toMqttMessage())
        }

        override fun connectionLost(error: Throwable) {
            onDisconnect(error)
        }
    }

    actual val isConnected: Boolean
        get() = client.isConnected


    actual suspend fun publish(topic: String, msg: MqttMessage, timeout: Long): MqttError? = try {
        logger.v { "publish" }
        client.publish(topic, PahoMqttMessage(msg.payload.toByteArray()))
        null
    } catch (mqttPersistenceEx: MqttPersistenceException) {
        MqttError(mqttPersistenceEx.message ?: "Message persistence failed.", MqttStatus.MSG_PERSISTENCE_FAILED)
    } catch (mqttEx: MqttException) {
        MqttError(mqttEx.message ?: "Message delivery failed.", MqttStatus.MSG_DELIVERY_FAILED)
    }

    @Suppress("unused", "RedundantSuspendModifier")
    /**
     * Subscribes to a topic.
     * @param topic The MQTT topic to use.
     * @param qos The MQTT quality of service to use.
     * @return Will return a [error][MqttError] if a problem has occurred. If a [error][MqttError] is returned then the
     * [subscribe failed][MqttStatus.SUBSCRIBE_FAILED] status (via [MqttError.statusCode]) is used.
     */
    actual suspend fun subscribe(topic: String, qos: MqttQos): MqttError? = try {
        logger.v { "subscribe" }
        client.subscribe(topic, qos.value)
        null
    } catch (ex: MqttException) {
        MqttError(ex.message ?: "Subscription failed.", MqttStatus.SUBSCRIBE_FAILED)
    }

    @Suppress("unused", "RedundantSuspendModifier")
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

    @Suppress("unused", "RedundantSuspendModifier")
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
    actual suspend fun connect(connOptions: MqttConnectionOptions): MqttError? {
        logger.v { "connect" }
        var result: MqttError? = null
        if (!isConnected) {
            connectToBroker(connOptions.toPhaoConnectOptions())
        } else {
            result = MqttError("Cannot connect to MQTT Broker.", MqttStatus.ALREADY_CONNECTED)
        }
        return result
    }

    /** Makes a attempt to establish a connection to the MQTT broker. */
    private fun connectToBroker(connOptions: PahoConnectOptions): MqttError? {
        logger.v { "connectToBroker" }
        var result: MqttError? = null
        var status = MqttStatus.SUCCESS
        try {
            client.connect(connOptions)
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

    private fun MqttConnectionOptions.toPhaoConnectOptions(): PahoConnectOptions {
        return PahoConnectOptions().apply {
            isCleanSession = cleanSession
            keepAliveInterval = keepAliveInterval
            userName = connUsername.ifEmpty { null }
            password = connPassword.toCharArray()
            connectionTimeout = connectionTimeout
        }
    }

    private fun PahoMqttMessage.toMqttMessage(): MqttMessage {
        return MqttMessage(
            msgId = id,
            qos = MqttQos.createMqttQos(qos),
            payload = payload.decodeToString(),
            retained = isRetained,
            duplicate = isDuplicate
        )
    }
}
