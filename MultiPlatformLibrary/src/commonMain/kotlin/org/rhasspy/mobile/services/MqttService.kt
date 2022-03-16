package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.rhasspy.mobile.services.mqtt.MqttConnectionOptions
import org.rhasspy.mobile.services.mqtt.MqttMessage
import org.rhasspy.mobile.services.mqtt.MqttPersistence
import org.rhasspy.mobile.services.mqtt.MqttResultCallback
import org.rhasspy.mobile.services.mqtt.native.MqttClient
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.math.min
import kotlin.native.concurrent.ThreadLocal

//TODO send events to mqtt (wake word detected ... then use session id for transcription)
@ThreadLocal
object MqttService {
    private val logger = Logger.withTag(this::class.simpleName!!)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var client: MqttClient? = null

    private val connected = MutableLiveData(false)
    val isConnected = connected.readOnly()


    private const val startSession = "hermes/dialogueManager/startSession"
    private const val continueSession = "hermes/dialogueManager/continueSession"
    private const val endSession = "hermes/dialogueManager/endSession"

    private const val sessionStarted = "hermes/dialogueManager/sessionStarted"
    private const val sessionEnded = "hermes/dialogueManager/sessionEnded"

    private const val toggleOn = "hermes/hotword/toggleOn"
    private const val toggleOff = "hermes/hotword/toggleOff"
    private const val setVolume = "rhasspy/audioServer/setVolume"
    private var playBytes = "hermes/audioServer/${ConfigurationSettings.siteId.data}/playBytes/#"

    private const val say = "hermes/tts/say"
    private const val intentRecognition = "hermes/nlu/query"
    private var remotePlay = "hermes/audioServer/${ConfigurationSettings.siteId.data}/playBytes/0"

    private const val sayFinished = "hermes/tts/sayFinished"
    private const val intentRecognized = "hermes/intent/#"
    private const val intentNotRecognized = "hermes/nlu/intentNotRecognized"
    private var playFinished = "hermes/audioServer/${ConfigurationSettings.siteId.data}/playFinished"

    private const val asrStartListening = "hermes/asr/startListening"
    private const val asrStopListening = "hermes/asr/stopListening"
    private var asrAudioSessionFrame = "hermes/audioServer/${ConfigurationSettings.siteId.data}/audioFrame"
    private const val asrTextCaptured = " hermes/asr/textCaptured"
    private const val asrError = "hermes/error/asr"


    private val callbacks = mutableListOf<MqttResultCallback>()

    fun start() {
        if (!ConfigurationSettings.isMQTTEnabled.data) {
            logger.v { "mqtt not enabled" }
            return
        }

        logger.d { "start" }
        playBytes = "hermes/audioServer/${ConfigurationSettings.siteId.data}/playBytes"
        playFinished = "hermes/audioServer/${ConfigurationSettings.siteId.data}/playFinished"
        remotePlay = "hermes/audioServer/${ConfigurationSettings.siteId.data}/playBytes/0"
        asrAudioSessionFrame = "hermes/audioServer/${ConfigurationSettings.siteId.data}/audioFrame"

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

                    arrayOf(
                        startSession,
                        continueSession,
                        endSession,
                        toggleOn,
                        toggleOff,
                        playBytes,
                        setVolume,
                        sayFinished,
                        asrStartListening,
                        asrStopListening
                    ).forEach { topic ->
                        subscribe(topic)?.also {
                            logger.e { "subscribe $topic \n${it.statusCode.name} ${it.msg}" }
                        }
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

    private fun onDelivered(token: Int) {
        logger.d { "onDelivered $token" }
    }

    private fun onMessageReceived(topic: String, message: MqttMessage) {
        //subSequence so we don't print super long wave data
        logger.v { "onMessageReceived $topic ${message.payload.toString().subSequence(1, min(message.payload.toString().length, 100))}" }
        try {
            val jsonObject = Json.decodeFromString<JsonObject>(message.payload.toString())

            if (checkSiteId(jsonObject) || topic == playBytes) {

                CoroutineScope(Dispatchers.Main).launch {
                    try { //coroutine catch
                        when (topic) {
                            toggleOn -> toggleOn(jsonObject)
                            toggleOff -> toggleOn(jsonObject)
                            startSession -> toggleOn(jsonObject)
                            endSession -> toggleOn(jsonObject)
                            setVolume -> setVolume(jsonObject)
                        }
                    } catch (e: Exception) {
                        logger.e(e) { "onMessageReceived error" }
                    }
                }

            } else {

                CoroutineScope(Dispatchers.Default).launch {
                    //check the other topics that don't require site id
                    try { //coroutine catch
                        when (topic) {
                            playBytes -> jsonObject["wav_bytes"]?.jsonObject?.get("data")?.also { data ->
                                ServiceInterface.playAudio(data.jsonArray.map { it.toString().toUInt().toByte() }
                                    .toByteArray())
                            } ?: run {
                                logger.e { "playBytes invalid value" }
                            }
                            intentRecognized,
                            intentNotRecognized,
                            sayFinished,
                            playFinished,
                            asrTextCaptured -> {
                                val uuid = Json.decodeFromString<JsonObject>(message.payload.toString())["id"]!!.jsonPrimitive.content
                                callbacks.firstOrNull {
                                    //startsWith for wildcards
                                    it.resultTopics.any { t -> topic.startsWith(t.replace("#", "")) }
                                            && it.uuid.toString() == uuid
                                }?.apply {
                                    callbacks.remove(this)
                                    callback.invoke(message)
                                }
                            }
                            else -> logger.d("received message on $topic but for different siteId ${jsonObject["siteId"].toString()}")
                        }
                    } catch (e: Exception) {
                        logger.e(e) { "onMessageReceived error" }
                    }
                }

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

    //###################################  Input Messages

    /**
     * https://rhasspy.readthedocs.io/en/latest/reference/#dialoguemanager_startsession
     *
     * hermes/dialogueManager/startSession (JSON)
     * Starts a new dialogue session (done automatically on hotword detected)
     * siteId: string required - Hermes site ID
     *
     * Response(s)
     * hermes/dialogueManager/sessionStarted
     */
    fun startSession(jsonObject: JsonObject) {
        ServiceInterface.startRecording()
    }

    /**
     * https://rhasspy.readthedocs.io/en/latest/reference/#dialoguemanager_continuesession
     *
     * hermes/dialogueManager/continueSession (JSON)
     * Requests that a session be continued after an intent has been recognized
     * sessionId: string - current session ID (required)
     */
    fun continueSession(jsonObject: JsonObject) {
        //TODO
    }

    /**
     * https://rhasspy.readthedocs.io/en/latest/reference/#dialoguemanager_endsession
     *
     * hermes/dialogueManager/endSession (JSON)
     * Requests that a session be terminated nominally
     * sessionId: string - current session ID (required)
     */
    suspend fun endSession(jsonObject: JsonObject) {
        ServiceInterface.stopRecording(jsonObject["sessionId"]?.jsonPrimitive?.content)
    }

    //###################################  Output Messages

    /**
     * hermes/dialogueManager/sessionStarted (JSON)
     * Indicates a session has started
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     *
     * Response to [hermes/dialogueManager/startSession]
     * Also used when session has started for other reasons
     */
    fun sessionStarted(uuid: Uuid) {
        coroutineScope.launch {
            client?.publish(
                sessionStarted, MqttMessage(
                    payload = Json.encodeToString(buildJsonObject {
                        put("sessionId", uuid.toString())
                        put("siteId", ConfigurationSettings.siteId.data)
                    })
                )
            )?.also {
                logger.e { "sessionStarted $uuid \n${it.statusCode.name} ${it.msg}" }
            }
        }
    }

    /**
     * hermes/dialogueManager/sessionEnded (JSON)
     * Indicates a session has terminated
     *
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     *
     * Response to hermes/dialogueManager/endSession or other reasons for a session termination
     */
    fun sessionEnded(uuid: Uuid) {
        coroutineScope.launch {
            client?.publish(
                sessionEnded, MqttMessage(
                    payload = Json.encodeToString(buildJsonObject {
                        put("sessionId", uuid.toString())
                        put("siteId", ConfigurationSettings.siteId.data)
                    })
                )
            )?.also {
                logger.e { "sessionEnded $uuid \n${it.statusCode.name} ${it.msg}" }
            }
        }
    }


    suspend fun toggleOn(jsonObject: JsonObject) {
        ServiceInterface.setListenForWake(true)
    }


    suspend fun toggleOff(jsonObject: JsonObject) {
        ServiceInterface.setListenForWake(false)
    }

    suspend fun setVolume(jsonObject: JsonObject) {
        jsonObject["volume"]?.jsonPrimitive?.floatOrNull?.also {
            ServiceInterface.setVolume(it)
        } ?: run {
            logger.e { "setVolume invalid value ${jsonObject["volume"]}" }
        }
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
    suspend fun textToSpeak(text: String): MqttMessage? {
        val uuid = uuid4()

        return client?.publish(
            say, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("text", text)
                    put("id", uuid.toString())
                    put("siteId", ConfigurationSettings.siteId.data)
                })
            )
        )?.let {
            logger.e { "textToSpeak $text \n${it.statusCode.name} ${it.msg}" }
            null
        } ?: run {
            mqttResult(uuid, arrayOf(sayFinished))
        }
    }

    /**
     * hermes/nlu/query (JSON)
     * Request an intent to be recognized from text
     * input: string - text to recognize intent from (required)
     * intentFilter: string? = null - valid intent names (null means all)
     * id: string? = null - unique id for request (copied to response messages)
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     * asrConfidence: float? = null - confidence from ASR system for input text
     * Response(s)
     * hermes/intent/<intentName>
     * hermes/nlu/intentNotRecognized
     */
    suspend fun intentRecognition(text: String): MqttMessage? {
        val uuid = uuid4()

        return client?.publish(
            intentRecognition, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("input", JsonPrimitive(text))
                    put("id", uuid.toString())
                    put("siteId", ConfigurationSettings.siteId.data)
                })
            )
        )?.let {
            logger.e { "intentRecognition $text \n${it.statusCode.name} ${it.msg}" }
            null
        } ?: run {
            mqttResult(uuid, arrayOf(intentRecognized, intentNotRecognized))
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
    suspend fun playWav(data: ByteArray): MqttMessage? {
        val uuid = uuid4()

        return client?.publish(remotePlay, MqttMessage(data))?.let {
            logger.e { "playWav ${data.size} \n${it.statusCode.name} ${it.msg}" }
            null
        } ?: run {
            mqttResult(uuid, arrayOf(playFinished))
        }
    }

    /**
     * hermes/asr/startListening (JSON)
     * Tell ASR system to start recording/transcribing
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     * stopOnSilence: bool = true - detect silence and automatically end voice command (Rhasspy only)
     * sendAudioCaptured: bool = false - send audioCaptured after stop listening (Rhasspy only)
     * wakewordId: string? = null - id of wake word that triggered session (Rhasspy only)
     */
    suspend fun startListening(uuid: Uuid) {
        //start listening
        client?.publish(
            asrStartListening, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("sessionId", uuid.toString())
                    put("stopOnSilence", false)
                    put("sendAudioCaptured", true)
                    put("siteId", ConfigurationSettings.siteId.data)
                })
            )
        )?.also {
            //didn't work
            logger.e { "asrStartListening ${uuid} \n${it.statusCode.name} ${it.msg}" }
        }
    }

    /**
     * hermes/audioServer/<siteId>/<sessionId>/audioSessionFrame (binary)
     * Chunk of WAV audio data for session
     * wav_bytes: bytes - WAV data to play (message payload)
     * siteId: string - Hermes site ID (part of topic)
     * sessionId: string - session ID (part of topic)
     */
    suspend fun audioSessionFrame(uuid: Uuid, data: ByteArray) {
        //start listening
        client?.publish(
            asrAudioSessionFrame.replace("#", uuid.toString()),
            MqttMessage(data)
        )?.also {
            //didn't work
            logger.e { "asrAudioSessionFrame ${data.size} \n${it.statusCode.name} ${it.msg}" }
        }
    }

    /**
     * hermes/asr/stopListening (JSON)
     * Tell ASR system to stop recording
     * Emits textCaptured if silence has was not detected earlier
     * siteId: string = "default" - Hermes site ID
     * sessionId: string = "" - current session ID
     */
    suspend fun stopListening(uuid: Uuid): MqttMessage? {

        //stop listening and await result
        return client?.publish(
            asrStopListening, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("sessionId", uuid.toString())
                    put("siteId", ConfigurationSettings.siteId.data)
                })
            )
        )?.let {
            //didn't work
            logger.e { "asrStopListening $uuid \n${it.statusCode.name} ${it.msg}" }
            null
        } ?: run {
            mqttResult(uuid, arrayOf(asrError, asrTextCaptured))
        }

        /*
        //stop listening and await result
        return client?.publish(
            "rhasspy/asr/recordingFinished", MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("sessionId", uuid.toString())
                    put("siteId", ConfigurationSettings.baseSiteId.data)
                })
            )
        )?. {
            //didn't work
            logger.e { "recordingFinished ${data.size} \n${it.statusCode.name} ${it.msg}" }
            null
        } ?: run {
            mqttResult(uuid, arrayOf(asrError, asrTextCaptured))
        }*/
    }


    /**
     * used to retrieve mqtt result on specific topic with specific id
     */
    private suspend fun mqttResult(uuid: Uuid, resultTopics: Array<String>): MqttMessage? {
        var result: MqttMessage? = null

        val job = Job()

        val callback = MqttResultCallback(uuid, resultTopics) {
            result = it
            job.complete()
        }

        callbacks.add(callback)

        return try {
            withTimeout(5000) {
                job.join()
                result
            }
        } catch (e: Exception) {
            callbacks.remove(callback)
            null
        }
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
