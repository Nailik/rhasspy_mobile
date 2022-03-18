package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import com.benasher44.uuid.Uuid
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.rhasspy.mobile.services.dialogue.ServiceInterface
import org.rhasspy.mobile.services.mqtt.*
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
    private val callbacks = mutableListOf<MqttResultCallback>()


    /**
     * start client externaly, only starts if mqtt is enabled
     *
     * creates new client
     * connects client to server
     *
     * subscribes to topics necessary if connection was successful
     *
     * sets connected value
     */
    fun start() {
        if (!ConfigurationSettings.isMQTTEnabled.data) {
            logger.v { "mqtt not enabled" }
            return
        }

        logger.d { "start" }

        //setup client
        createClient()

        coroutineScope.launch {
            //connect client
            if (connectClient()) {
                subscribeTopics()
            } else {
                logger.e { "client not connected after attempt" }
            }

            CoroutineScope(Dispatchers.Main).launch {
                connected.value = client?.isConnected == true
            }
        }
    }

    /**
     * stops client
     *
     * disconnects, resets connected value and deletes client object
     */
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

    /**
     * creates new client according to settings
     */
    private fun createClient() {
        logger.v { "createClient" }

        client = MqttClient(
            brokerUrl = "tcp://${ConfigurationSettings.mqttHost.data}:${ConfigurationSettings.mqttPort.data}",
            clientId = ConfigurationSettings.siteId.data,
            persistenceType = MqttPersistence.MEMORY,
            onDelivered = { },
            onMessageReceived = { topic, message -> onMessageReceived(topic, message) },
            onDisconnect = { error -> onDisconnect(error) },
        )
    }

    /**
     * connects client to server and returns if client is now connected
     */
    private suspend fun connectClient(): Boolean {
        logger.v { "connectClient" }
        //connect to client
        client?.connect(
            MqttConnectionOptions(
                connUsername = ConfigurationSettings.mqttUserName.data,
                connPassword = ConfigurationSettings.mqttPassword.data
            )
        )?.also {
            logger.e { "connect \n${it.statusCode.name} ${it.msg}" }
        }
        return client?.isConnected == true
    }

    /**
     * Subscribes to topics that are necessary
     */
    private suspend fun subscribeTopics() {
        logger.v { "subscribeTopics" }

        MQTTTopicsSubscription.values().forEach { topic ->
            client?.subscribe(topic.topic)?.also {
                logger.e { "subscribe $topic \n${it.statusCode.name} ${it.msg}" }
            }
        }
    }


    private fun onMessageReceived(topic: String, message: MqttMessage) {
        //subSequence so we don't print super long wave data
        logger.v { "onMessageReceived $topic ${message.payload.toString().subSequence(1, min(message.payload.toString().length, 100))}" }

        try {

            when (MQTTTopicsSubscription.valueOf(topic)) {
                MQTTTopicsSubscription.StartSession -> startSession(message)
                MQTTTopicsSubscription.EndSession -> endSession(message)
                MQTTTopicsSubscription.SessionStarted -> sessionStarted(message)
                MQTTTopicsSubscription.HotWordToggleOn -> hotWordToggleOn(message)
                MQTTTopicsSubscription.HotWordToggleOff -> hotWordToggleOff(message)
                MQTTTopicsSubscription.AsrStartListening -> asrStartListening(message)
                MQTTTopicsSubscription.AsrStopListening -> asrStopListening(message)
                MQTTTopicsSubscription.AsrTextCaptured -> asrTextCaptured(message)
                MQTTTopicsSubscription.AsrError -> asrError(message)
                MQTTTopicsSubscription.IntentRecognitionResult -> intentRecognitionResult(message)
                MQTTTopicsSubscription.IntentNotRecognized -> intentNotRecognized(message)
                MQTTTopicsSubscription.IntentHandlingToggleOn -> intentHandlingToggleOn(message)
                MQTTTopicsSubscription.IntentHandlingToggleOff -> intentHandlingToggleOff(message)
                MQTTTopicsSubscription.SayFinished -> sayFinished(message)
                MQTTTopicsSubscription.AudioOutputToggleOff -> audioOutputToggleOff(message)
                MQTTTopicsSubscription.AudioOutputToggleOn -> audioOutputToggleOn(message)
                MQTTTopicsSubscription.SetVolume -> setVolume(message)
            }

        } catch (e: Exception) {
            logger.e(e) { "onMessageReceived error" }
        }
    }

    private fun JsonObject.isThisSiteId(): Boolean {
        val siteId = this["siteId"]?.jsonPrimitive?.content
        logger.v { "siteId is $siteId" }
        return siteId == ConfigurationSettings.siteId.data
    }

    //###################################  Input Messages

    /**
     * https://rhasspy.readthedocs.io/en/latest/reference/#dialoguemanager_startsession
     *
     * hermes/dialogueManager/startSession (JSON)
     * Starts a new dialogue session (done automatically on hotword detected)
     *
     * siteId: string = "default" - Hermes site ID
     *
     * Response(s)
     * hermes/dialogueManager/sessionStarted
     * hermes/dialogueManager/sessionQueued
     */
    private fun startSession(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.toString())

        if (jsonObject.isThisSiteId()) {
            ServiceInterface.startSession()
        } else {
            logger.d { "received startSession but for other siteId" }
        }
    }

    /**
     * https://rhasspy.readthedocs.io/en/latest/reference/#dialoguemanager_endsession
     *
     * hermes/dialogueManager/endSession (JSON)
     * Requests that a session be terminated nominally
     * sessionId: string - current session ID (required)
     */
    private fun endSession(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.toString())

        if (jsonObject.isThisSiteId()) {
            ServiceInterface.endSession(jsonObject["sessionId"]?.jsonPrimitive?.content)
        } else {
            logger.d { "received endSession but for other siteId" }
        }
    }

    /**
     * hermes/dialogueManager/sessionStarted (JSON)
     * Indicates a session has started
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     *
     * Response to [hermes/dialogueManager/startSession]
     * Also used when session has started for other reasons
     *
     * used to save the sessionId and check for it when recording etc
     */
    private fun sessionStarted(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.toString())

        if (jsonObject.isThisSiteId()) {
            jsonObject["sessionId"]?.jsonPrimitive?.content?.also {
                ServiceInterface.sessionStarted(it)
            } ?: run {
                logger.d { "received sessionStarted with empty session Id" }
            }
        } else {
            logger.d { "received endSession but for other siteId" }
        }
    }

    /**
     * hermes/dialogueManager/sessionStarted (JSON)
     * Indicates a session has started
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     *
     * Response to [hermes/dialogueManager/startSession]
     * Also used when session has started for other reasons
     */
    suspend fun sessionStarted(sessionId: String) {
        coroutineScope.launch {
            client?.publish(
                MQTTTopicsPublish.SessionStarted.topic, MqttMessage(
                    payload = Json.encodeToString(buildJsonObject {
                        put("sessionId", sessionId)
                        put("siteId", ConfigurationSettings.siteId.value)
                    })
                )
            )?.also {
                logger.e { "unable to publish sessionStarted $sessionId \n${it.statusCode.name} ${it.msg}" }
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
    suspend fun sessionEnded(sessionId: String) {
        coroutineScope.launch {
            client?.publish(
                MQTTTopicsPublish.SessionEnded.topic, MqttMessage(
                    payload = Json.encodeToString(buildJsonObject {
                        put("sessionId", sessionId)
                        put("siteId", ConfigurationSettings.siteId.data)
                    })
                )
            )?.also {
                logger.e { "unable to publish sessionEnded $sessionId \n${it.statusCode.name} ${it.msg}" }
            }
        }
    }


    private fun hotWordToggleOn(message: MqttMessage) {

    }

    private fun hotWordToggleOff(message: MqttMessage) {

    }

    private fun asrStartListening(message: MqttMessage) {

    }

    private fun asrStopListening(message: MqttMessage) {
        ServiceInterface.stopRecording()
    }

    private fun asrTextCaptured(message: MqttMessage) {
        ServiceInterface.textCaptured()
    }

    private fun asrError(message: MqttMessage) {

    }

    private fun intentRecognitionResult(message: MqttMessage) {

    }

    private fun intentNotRecognized(message: MqttMessage) {

    }

    private fun intentHandlingToggleOn(message: MqttMessage) {

    }

    private fun intentHandlingToggleOff(message: MqttMessage) {

    }

    private fun sayFinished(message: MqttMessage) {

    }

    private fun audioOutputToggleOff(message: MqttMessage) {

    }

    private fun audioOutputToggleOn(message: MqttMessage) {

    }

    private fun setVolume(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.toString())
        jsonObject["volume"]?.jsonPrimitive?.floatOrNull?.also {
            ServiceInterface.setVolume(it)
        } ?: run {
            logger.e { "setVolume invalid value ${jsonObject["volume"]}" }
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

    //###################################  Output Messages
    //when MQTT is not enabled client will be null and nothing will be published

    suspend fun sessionIntentNotRecognized() {

    }


    fun audioFrame(byteArray: ByteArray) {

    }

    /**
     * hermes/audioServer/<siteId>/<sessionId>/audioSessionFrame (binary)
     * Chunk of WAV audio data for session
     * wav_bytes: bytes - WAV data to play (message payload)
     * siteId: string - Hermes site ID (part of topic)
     * sessionId: string - session ID (part of topic)
     */
    suspend fun asrAudioSessionFrame() {
        /*
        //start listening
        client?.publish(
            asrAudioSessionFrame.replace("#", uuid.toString()),
            MqttMessage(data)
        )?.also {
            //didn't work
            logger.e { "asrAudioSessionFrame ${data.size} \n${it.statusCode.name} ${it.msg}" }
        }
*/
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
    private fun asrStartListeningOutput(message: MqttMessage) {
        /*
        //start listening
        client?.publish(
            asrStartListening, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("siteId", ConfigurationSettings.baseSiteId.data)
                    put("sessionId", uuid.toString())
                    put("lang", JsonNull)
                    put("stopOnSilence", true)
                    put("sendAudioCaptured", true)
                    put("wakeWordId", "default")
                    put("intentFilter", JsonNull)
                })
            )
        )?.also {
            //didn't work
            logger.e { "asrStartListening ${uuid} \n${it.statusCode.name} ${it.msg}" }
        }
         */
    }

    /**
     * hermes/asr/stopListening (JSON)
     * Tell ASR system to stop recording
     * Emits textCaptured if silence has was not detected earlier
     * siteId: string = "default" - Hermes site ID
     * sessionId: string = "" - current session ID
     */
    private fun asrStopListeningOutput(message: MqttMessage) {
/*
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
        */

    }

    suspend fun wakeWordDetected() {
        /*  coroutineScope.launch {
              client?.publish(
                  wakeWordDetected, MqttMessage(
                      payload = Json.encodeToString(buildJsonObject {
                          put("modelId", "default")
                          put("modelVersion", "")
                          put("modelType", "personal")
                          put("currentSensitivity", ConfigurationSettings.wakeWordKeywordSensitivity.data)
                          put("siteId", ConfigurationSettings.siteId.data)
                          put("sessionId", JsonNull)
                          put("sendAudioCaptured", JsonNull)
                          put("lang", JsonNull)
                          put("customEntities", JsonNull)
                      })
                  )
              )?.also {
                  logger.e { "wakeWordDetected \n${it.statusCode.name} ${it.msg}" }
              }
          }

         */
    }

    suspend fun audioCaptured() {

    }

    suspend fun intentRecognition(text: String) {
/*
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

 */
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
    suspend fun say(text: String) {
        //set volume to 0 so it's only send here??
        //play bytes request id
        //maybe set this as site id to play on??
        /* val uuid = uuid4()

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

         */
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
    suspend fun audioOutputPlayBytes(data: ByteArray) {
        /*val uuid = uuid4()

        return client?.publish(remotePlay, MqttMessage(data))?.let {
            logger.e { "playWav ${data.size} \n${it.statusCode.name} ${it.msg}" }
            null
        } ?: run {
            mqttResult(uuid, arrayOf(playFinished))
        }

         */
    }


    suspend fun audioOutputPlayFinished(data: ByteArray) {
        /* val uuid = uuid4()

         return client?.publish(remotePlay, MqttMessage(data))?.let {
             logger.e { "playWav ${data.size} \n${it.statusCode.name} ${it.msg}" }
             null
         } ?: run {
             mqttResult(uuid, arrayOf(playFinished))
         }

         */
    }

    fun playBytes(data: ByteArray) {

    }


    fun playFinished() {

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

    fun hotWordDetected() {


    }

    fun hotWordError(description: String) {


    }
}


/*

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
                            asrTextCaptured -> asrTextCaptured(jsonObject)
                            asrStopListening -> asrStopListening(jsonObject)
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
 */