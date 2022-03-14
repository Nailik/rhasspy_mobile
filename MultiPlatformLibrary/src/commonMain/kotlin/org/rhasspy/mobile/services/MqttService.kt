package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
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
    private const val setVolume = "rhasspy/audioServer/setVolume"
    private var playBytes = "hermes/audioServer/${ConfigurationSettings.siteId.data}/playBytes/#"

    private const val say = "hermes/tts/say"
    private const val intentRecognition = "hermes/nlu/query"
    private const val remotePlay = "hermes/audioServer/default/playBytes/0"

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

                    subscribe(setVolume)?.also {
                        logger.e { "subscribe $setVolume \n${it.statusCode.name} ${it.msg}" }
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
        //subSequence so we don't print super long wave data
        logger.v { "onMessageReceived $topic ${message.payload.subSequence(1, 100)}" }
        try {
            val jsonObject = Json.decodeFromString<JsonObject>(message.payload)

            if (checkSiteId(jsonObject) || topic == playBytes) {

                CoroutineScope(Dispatchers.Main).launch {
                    try { //coroutine catch
                        when (topic) {
                            toggleOn -> ServiceInterface.setListenForWake(true)
                            toggleOff -> ServiceInterface.setListenForWake(false)
                            startSession -> ServiceInterface.startRecording()
                            endSession -> ServiceInterface.stopRecording()
                            setVolume -> jsonObject["volume"]?.jsonPrimitive?.floatOrNull?.also {
                                ServiceInterface.setVolume(it)
                            } ?: run {
                                logger.e { "setVolume invalid value ${jsonObject["volume"]}" }
                            }
                            playBytes -> jsonObject["wav_bytes"]?.jsonObject?.get("data")?.also { data ->
                                ServiceInterface.playAudio(data.jsonArray.map { it.toString().toUInt().toByte() }
                                    .toByteArray())
                            } ?: run {
                                logger.e { "playBytes invalid value" }
                            }
                        }
                    } catch (e: Exception) {
                        logger.e(e) { "onMessageReceived error" }
                    }
                }
            } else {
                logger.d("received message on $topic but for different siteId ${jsonObject["siteId"].toString()}")
            }
        } catch (e: Exception) {
            logger.e(e) { "onMessageReceived error" }
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
        return jsonObject["siteId"]?.jsonPrimitive?.content == ConfigurationSettings.siteId.data
    }

    /**
     * hermes/tts/say (JSON)
     * Generate spoken audio for a sentence using the configured text to speech system
     * Automatically sends playBytes
     * playBytes.requestId = say.id
     * text: string - sentence to speak (required)
     * lang: string? = null - override language for TTS system
     * id: string? = null - unique ID for request (copied to sayFinished)
     * volume: float? = null - volume level to speak with (0 = off, 1 = full volume)
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     * Response(s)
     * hermes/tts/sayFinished (JSON)
     */
    suspend fun textToSpeak(text: String) {
        client?.publish(
            say, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("text", JsonPrimitive(text))
                })
            )
        )?.also {
            logger.e { "textToSpeak $text \n${it.statusCode.name} ${it.msg}" }
        }
    }

    /**
     * hermes/nlu/query (JSON)
     * Request an intent to be recognized from text
     * input: string - text to recognize intent from (required)
     * intentFilter: [string]? = null - valid intent names (null means all)
     * id: string? = null - unique id for request (copied to response messages)
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     * asrConfidence: float? = null - confidence from ASR system for input text
     * Response(s)
     * hermes/intent/<intentName>
     * hermes/nlu/intentNotRecognized
     */
    suspend fun intentRecognition(text: String) {
        client?.publish(
            intentRecognition, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("input", JsonPrimitive(text))
                })
            )
        )?.also {
            logger.e { "intentRecognition $text \n${it.statusCode.name} ${it.msg}" }
        }
    }


    /**
     * hermes/audioServer/<siteId>/playBytes/<requestId> (JSON)
     * Play WAV data
     * wav_bytes: bytes - WAV data to play (message payload)
     * requestId: string - unique ID for request (part of topic)
     * siteId: string - Hermes site ID (part of topic)
     * Response(s)
     * hermes/audioServer/<siteId>/playFinished (JSON)
     */
    suspend fun playWav(data: ByteArray) {
        client?.publish(
            remotePlay, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("wav_bytes", buildJsonObject {
                        put("data", buildJsonArray {
                            data.forEach {
                                add(it)
                            }
                        })
                    })
                })
            )
        )?.also {
            logger.e { "playWav ${data.size} \n${it.statusCode.name} ${it.msg}" }
        }
    }

    /**
     * 1. tell ASR to transcripe
     * hermes/asr/startListening (JSON)
     * Tell ASR system to start recording/transcribing
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     * stopOnSilence: bool = true - detect silence and automatically end voice command (Rhasspy only)
     * sendAudioCaptured: bool = false - send audioCaptured after stop listening (Rhasspy only)
     * wakewordId: string? = null - id of wake word that triggered session (Rhasspy only)
     *
     * 2. send WAV data in chunks (size to test)
     *
     * hermes/audioServer/<siteId>/<sessionId>/audioSessionFrame (binary)
     * Chunk of WAV audio data for session
     * wav_bytes: bytes - WAV data to play (message payload)
     * siteId: string - Hermes site ID (part of topic)
     * sessionId: string - session ID (part of topic)
     *
     * 3. when finished tell ASR system to stop
     *
     * hermes/asr/stopListening (JSON)
     * Tell ASR system to stop recording
     * Emits textCaptured if silence has was not detected earlier
     * siteId: string = "default" - Hermes site ID
     * sessionId: string = "" - current session ID
     *
     *
     */
    suspend fun speechToText(data: ByteArray) {

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
