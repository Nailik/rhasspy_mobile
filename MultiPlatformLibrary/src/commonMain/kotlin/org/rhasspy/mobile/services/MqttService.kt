package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.rhasspy.mobile.services.mqtt.MqttConnectionOptions
import org.rhasspy.mobile.services.mqtt.MqttMessage
import org.rhasspy.mobile.services.mqtt.MqttPersistence
import org.rhasspy.mobile.services.mqtt.native.MqttClient
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object MqttService {
    private val logger = Logger.withTag(this::class.simpleName!!)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var client: MqttClient? = null

    private val connected = MutableLiveData(false)
    val isConnected = connected.readOnly()

    private const val toggleOn = "hermes/hotword/toggleOn"
    private const val toggleOff = "hermes/hotword/toggleOff"
    private const val startSession = "hermes/dialogueManager/startSession"
    private const val endSession = "hermes/dialogueManager/endSession"
    private var playBytes = "hermes/audioServer/${ConfigurationSettings.siteId.data}/playBytes"

    fun start() {
        logger.d { "start" }
        playBytes = "hermes/audioServer/${ConfigurationSettings.siteId.data}/playBytes"

        client = MqttClient(
            brokerUrl = "tcp://${ConfigurationSettings.mqttHost.data}:${ConfigurationSettings.mqttPort.data}",
            clientId = ConfigurationSettings.siteId.data,
            persistenceType = MqttPersistence.MEMORY,
            onDelivered = { token -> onDelivered(token) },
            onMessageReceived = { topic, message -> onMessageReceived(topic, message) },
            onDisconnect = { error -> onDisconnect(error) },
        )

        coroutineScope.launch {
            logger.d { "connect" }
            client?.connect(
                MqttConnectionOptions(
                    connUsername = ConfigurationSettings.mqttUserName.data,
                    connPassword = ConfigurationSettings.mqttPassword.data
                )
            )?.also {
                logger.e { "connect \n${it.statusCode.name} ${it.msg}" }
            }


            client?.apply {
                CoroutineScope(Dispatchers.Main).launch {
                    connected.value = isConnected
                }

                if (isConnected) {
                    logger.d { "successfully connected" }

                    subscribe(toggleOn)?.also {
                        logger.e { "subscribe $toggleOn \n${it.statusCode.name} ${it.msg}" }
                    }

                    subscribe(toggleOff)?.also {
                        logger.e { "subscribe $toggleOff \n${it.statusCode.name} ${it.msg}" }
                    }

                    subscribe(startSession)?.also {
                        logger.e { "subscribe $startSession \n${it.statusCode.name} ${it.msg}" }
                    }
                    subscribe(endSession)?.also {
                        logger.e { "subscribe $endSession \n${it.statusCode.name} ${it.msg}" }
                    }

                    subscribe(playBytes)?.also {
                        logger.e { "subscribe $playBytes \n${it.statusCode.name} ${it.msg}" }
                    }
                } else {
                    logger.e { "client not connected after attempt" }
                }
            }
        }
    }

    fun stop() {
        logger.d { "stop" }
        client?.apply {
            if (isConnected) {
                disconnect()?.also {
                    logger.e { "disconnect \n${it.statusCode.name} ${it.msg}" }
                }
            }
            connected.value = isConnected
        }
        client = null
    }

    fun publish() {

    }

    private fun onDelivered(token: Int) {
        logger.d { "onDelivered $token" }
    }

    private fun onMessageReceived(topic: String, message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload)

        if (checkSiteId(jsonObject)) {

            when (topic) {
                toggleOn -> ServiceInterface.setListenForWake(true)
                toggleOff -> ServiceInterface.setListenForWake(false)
                startSession -> ServiceInterface.startRecording()
                endSession -> ServiceInterface.stopRecording()
                playBytes -> ServiceInterface.playAudio(jsonObject["wav_bytes"].toString().toByteArray())
            }
        } else {
            logger.d("received message on $topic but for different siteId ${jsonObject["siteId"].toString()}")
        }
    }

    private fun onDisconnect(error: Throwable) {
        logger.e(error) { "onDisconnect" }

        client?.apply {
            if (!isConnected) {
                client = null
            }
            connected.value = isConnected
        }
    }

    private fun checkSiteId(jsonObject: JsonObject): Boolean {
        return jsonObject["siteId"].toString() == ConfigurationSettings.siteId.data
    }
}

//wakeword
//hermes/hotword/<wakewordId>/detected und site id
//ignoring when hermes/hotword/toggleOff

//toggle wakeword (like httpservice)
//hermes/hotword/toggleOff
//hermes/hotword/toggleOn


//speech to text
/*
what is needed? what is a session?

hermes/audioServer/<siteId>/audioFrame
WAV chunk from microphone for a site
hermes/audioServer/<siteId>/<sessionId>/audioSessionFrame
WAV chunk from microphone for a session
 */


//audio output
//hermes/audioServer/<siteId>/playBytes/<requestId>
//WAV audio to play through speakers
//
//hermes/audioServer/<siteId>/playFinished
//Audio has finished playing


//dialogue management
//after wake word found
/*
hermes/dialogueManager/startSession
Start a new session
silence detected
hermes/dialogueManager/endSession
End an existing session
 */


/*
hermes/dialogueManager/sessionStarted
New session has started

silence detected
hermes/dialogueManager/sessionEnded
Existing session has terminated
 */
