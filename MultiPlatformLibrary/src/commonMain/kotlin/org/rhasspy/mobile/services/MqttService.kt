package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
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
            client?.connect(
                MqttConnectionOptions(
                    userName = ConfigurationSettings.mqttUserName.data,
                    passWord = ConfigurationSettings.mqttPassword.data
                )
            )

            client?.apply {
                subscribe(toggleOn)
                subscribe(toggleOff)

                subscribe(startSession)
                subscribe(endSession)

                subscribe(playBytes)
            }
        }
    }

    fun stop() {
        logger.d { "stop" }
        client?.disconnect()
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
