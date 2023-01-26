package org.rhasspy.mobile.logic.nativeutils

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttPersistenceException
import org.eclipse.paho.client.mqttv3.MqttSecurityException
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import org.rhasspy.mobile.logic.fileutils.FolderType
import org.rhasspy.mobile.logic.mqtt.MqttError
import org.rhasspy.mobile.logic.mqtt.MqttMessage
import org.rhasspy.mobile.logic.mqtt.MqttPersistence
import org.rhasspy.mobile.logic.mqtt.MqttQos
import org.rhasspy.mobile.logic.mqtt.MqttStatus
import org.rhasspy.mobile.logic.readOnly
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceConnectionOptions
import java.io.File
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * Represents a MQTT client which can connect to the MQTT Broker.
 */
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

    /**
     * If *true* then there is a connection to the MQTT Broker.
     **/
    private val _isConnected = MutableStateFlow(false)
    actual val isConnected = _isConnected.readOnly

    /**
     * callback for connection, and message delivery and arriving
     */
    private val callback = object : MqttCallback {
        override fun deliveryComplete(token: IMqttDeliveryToken) {
            onDelivered(token.messageId)
        }

        override fun messageArrived(
            topic: String,
            message: org.eclipse.paho.client.mqttv3.MqttMessage
        ) {
            onMessageReceived(topic, message.toMqttMessage())
        }

        override fun connectionLost(error: Throwable) {
            _isConnected.value = false
            onDisconnect(error)
            client.setCallback(null)
        }
    }

    /**
     * to publish and audio frame
     */
    actual suspend fun publish(topic: String, msg: MqttMessage, timeout: Long): MqttError? = try {
        logger.v { "publish $topic $msg" }

        client.publish(topic, org.eclipse.paho.client.mqttv3.MqttMessage(msg.payload).apply {
            id = msg.msgId
            qos = msg.qos.value
            isRetained = msg.retained
        })

        null
    } catch (mqttPersistenceEx: MqttPersistenceException) {
        MqttError(
            mqttPersistenceEx.message ?: "Message persistence failed.",
            MqttStatus.MSG_PERSISTENCE_FAILED
        )
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

        val result = if (!client.isConnected) {
            connectToBroker(connOptions.toPahoConnectOptions())
        } else {
            MqttError("Cannot connect to MQTT Broker.", MqttStatus.ALREADY_CONNECTED)
        }
        _isConnected.value = client.isConnected
        return result
    }

    /**
     * Makes a attempt to establish a connection to the MQTT broker.
     **/
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
                _isConnected.value = false
                client.disconnect()
                client.setCallback(null)
            } catch (ex: MqttException) {
                result = MqttError(
                    "Cannot disconnect from MQTT Broker.",
                    MqttStatus.MSG_PERSISTENCE_FAILED
                )
            }
        }
        return result
    }

    /**
     * convert service connection options to paho connection option
     */
    private fun MqttServiceConnectionOptions.toPahoConnectOptions(): MqttConnectOptions {
        return MqttConnectOptions().also {
            if (this.isSSLEnabled) {
                it.socketFactory = createSSLContext(this.keyStoreFile).socketFactory
            }
            it.isCleanSession = this.cleanSession
            it.keepAliveInterval = this.keepAliveInterval
            it.userName = this.connUsername.ifEmpty { null }
            it.password = this.connPassword.toCharArray()
            it.connectionTimeout = this.connectionTimeout
        }
    }

    /**
     * convert paho MqttMessage to common MqttMessage
     */
    private fun org.eclipse.paho.client.mqttv3.MqttMessage.toMqttMessage(): MqttMessage {
        return MqttMessage(
            msgId = id,
            qos = MqttQos.createMqttQos(qos),
            payload = payload,
            retained = isRetained
        )
    }

    /**
     * create ssl context by reading keystore file
     */
    private fun createSSLContext(keyStoreFileName: String): SSLContext {
        val keyStore = KeyStore.getInstance("BKS")
        keyStore.load(
            File("${FolderType.CertificateFolder.Mqtt}/$keyStoreFileName").inputStream(),
            null
        )
        val factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        factory.init(keyStore)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, factory.trustManagers, null)
        return sslContext
    }

}