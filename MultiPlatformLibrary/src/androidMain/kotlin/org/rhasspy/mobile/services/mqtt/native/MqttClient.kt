package org.rhasspy.mobile.services.mqtt.native

import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttPersistenceException
import org.eclipse.paho.client.mqttv3.MqttSecurityException
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import org.rhasspy.mobile.services.mqtt.*
import org.eclipse.paho.client.mqttv3.MqttClient as PahoMqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions as PahoConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage as PahoMqttMessage

@ExperimentalStdlibApi
actual class MqttClient(
    @Suppress("MemberVisibilityCanBePrivate") actual val brokerUrl: String,
    @Suppress("MemberVisibilityCanBePrivate") actual val clientId: String = "Default",
    @Suppress("MemberVisibilityCanBePrivate") val persistenceType: Int = MqttPersistence.NONE
) {
    private var client = PahoMqttClient("tcp://test.com", "")
    actual var deliveryCompleteHandler: (Int) -> Unit = {}
    actual var connectionLostHandler: (String) -> Unit = {}
    actual var messageArrivedHandler: (String, MqttMessage) -> Unit = { _, _ -> }
    private val callback = MqttCallback(deliveryCompleteHandler, connectionLostHandler, messageArrivedHandler)
    actual val isConnected: Boolean
        get() = client.isConnected

    @Suppress("unused", "RedundantSuspendModifier")
    /**
     * Publishes a message to the MQTT Broker.
     * @param topic The MQTT topic to use.
     * @param msg The MQTT message which includes the payload.
     * @param timeout Timeout for publishing in milliseconds.
     * @return Will return a [error][MqttError] if a problem has occurred. If a [error][MqttError] is returned then one
     * of the following [status objects][MqttStatus] (via [MqttError.statusCode]) is included:
     * - [Persistence failed][MqttStatus.MSG_PERSISTENCE_FAILED]
     * - [Message delivery failed][MqttStatus.MSG_DELIVERY_FAILED]
     */
    actual suspend fun publish(topic: String, msg: MqttMessage, timeout: Long): MqttError? = try {
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
        var result: MqttError? = null
        if (!isConnected) {
            client = when (persistenceType) {
                MqttPersistence.MEMORY -> PahoMqttClient(brokerUrl, clientId, MemoryPersistence())
                MqttPersistence.FILE -> PahoMqttClient(brokerUrl, clientId, MqttDefaultFilePersistence())
                else -> PahoMqttClient(brokerUrl, clientId)
            }
            connectToBroker(createPahoConnectOptions(connOptions))
        } else {
            result = MqttError("Cannot connect to MQTT Broker.", MqttStatus.ALREADY_CONNECTED)
        }
        return result
    }

    /** Makes a attempt to establish a connection to the MQTT broker. */
    private fun connectToBroker(connOptions: PahoConnectOptions): MqttError? {
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
                else -> throw mqttEx
            }
        }
        if (status != MqttStatus.SUCCESS) result = MqttError("Cannot connect to MQTT Broker.", status)
        return result
    }

    @Suppress("unused", "RedundantSuspendModifier")
    /**
     * Disconnects from the MQTT Broker.
     * @return Will return a [error][MqttError] if a problem has occurred. If a [error][MqttError] is returned then the
     * [message persistence failed][MqttStatus.MSG_PERSISTENCE_FAILED] status (via [MqttError.statusCode]) is used.
     */
    actual suspend fun disconnect(): MqttError? {
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

    private fun createPahoConnectOptions(connOptions: MqttConnectionOptions) = PahoConnectOptions().apply {
        isCleanSession = connOptions.cleanSession
        keepAliveInterval = connOptions.keepAliveInterval
        userName = if (connOptions.username.isEmpty()) null else connOptions.username
        password = if (connOptions.password.isEmpty()) null else connOptions.password.toCharArray()
        connectionTimeout = connOptions.connectionTimeout
    }
}
