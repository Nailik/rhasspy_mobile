package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.rhasspy.mobile.data.AudioPlayingOptions
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.mqtt.*
import org.rhasspy.mobile.nativeutils.MqttClient
import org.rhasspy.mobile.observer.MutableObservable
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.math.min
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object MqttService {
    private val logger = Logger.withTag("MqttService")
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var client: MqttClient? = null

    private val connected = MutableObservable(false)
    val isConnected = connected.readOnly()

    private var isCurrentlyConnected: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                connected.value = value
            }
        }

    private const val id = 98489482

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
    suspend fun start() {
        if (!ConfigurationSettings.isMQTTEnabled.value) {
            logger.v { "mqtt not enabled" }
            return
        }

        logger.d { "start" }

        //setup client
        createClient()

        isCurrentlyConnected

        //connect client
        if (connectClient()) {
            subscribeTopics()
        } else {
            logger.e { "client not connected after attempt" }
        }
        isCurrentlyConnected = client?.isConnected == true
    }

    /**
     * stops client
     *
     * disconnects, resets connected value and deletes client object
     */
    suspend fun stop() {
        logger.d { "stop" }
        client?.apply {
            if (isCurrentlyConnected) {
                disconnect()?.also {
                    logger.e { "disconnect \n${it.statusCode.name} ${it.msg}" }
                }
            }
            isCurrentlyConnected = isConnected
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
            clientId = ConfigurationSettings.siteId.value,
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
                connUsername = ConfigurationSettings.mqttUserName.value,
                connPassword = ConfigurationSettings.mqttPassword.value
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

        //subscribe to topics with this site id (if contained in topic, currently only in PlayBytes)
        MQTTTopicsSubscription.values().forEach { topic ->
            client?.subscribe(
                topic.topic
                    .replace("<siteId>", ConfigurationSettings.siteId.value)
            )?.also {
                logger.e { "subscribe $topic \n${it.statusCode.name} ${it.msg}" }
            }
        }
    }

    private fun onDisconnect(error: Throwable) {
        logger.e(error) { "onDisconnect" }

        client?.apply {
            if (!isCurrentlyConnected) {
                client = null
            }
            isCurrentlyConnected = isConnected
        }
    }

    private fun onMessageReceived(topic: String, message: MqttMessage) {
        //subSequence so we don't print super long wave data
        logger.v {
            "onMessageReceived id ${message.msgId} $topic ${
                message.payload.decodeToString().subSequence(1, min(message.payload.decodeToString().length, 300))
            }"
        }

        if (message.msgId == id) {
            //ignore all messages that i have send
            logger.v { "message ignored, was same id as send by myself" }
            return
        }

        try {
            when (MQTTTopicsSubscription.fromTopic(topic)) {
                MQTTTopicsSubscription.StartSession -> startSession(message)
                MQTTTopicsSubscription.EndSession -> endSession(message)
                MQTTTopicsSubscription.SessionStarted -> sessionStarted(message)
                MQTTTopicsSubscription.SessionEnded -> sessionEnded(message)
                MQTTTopicsSubscription.HotWordToggleOn -> hotWordToggleOn(message)
                MQTTTopicsSubscription.HotWordToggleOff -> hotWordToggleOff(message)
                MQTTTopicsSubscription.AsrStartListening -> startListening(message)
                MQTTTopicsSubscription.AsrStopListening -> stopListening(message)
                MQTTTopicsSubscription.AsrTextCaptured -> asrTextCaptured(message)
                MQTTTopicsSubscription.AsrError -> asrError(message)
                MQTTTopicsSubscription.IntentNotRecognized -> intentNotRecognized(message)
                MQTTTopicsSubscription.IntentHandlingToggleOn -> intentHandlingToggleOn(message)
                MQTTTopicsSubscription.IntentHandlingToggleOff -> intentHandlingToggleOff(message)
                MQTTTopicsSubscription.AudioOutputToggleOff -> audioOutputToggleOff(message)
                MQTTTopicsSubscription.AudioOutputToggleOn -> audioOutputToggleOn(message)
                MQTTTopicsSubscription.SetVolume -> setVolume(message)
                else -> {
                    when {
                        MQTTTopicsSubscription.PlayBytes.topic
                            .replace("<siteId>", ConfigurationSettings.siteId.value)
                            .replace("/", "\\/") //escape slashes
                            .replace("+", ".*") //replace wildcard with regex text
                            .toRegex()
                            .matches(topic) -> {
                            playBytes(message)
                        }
                        MQTTTopicsSubscription.HotWordDetected.topic
                            .replace("/", "\\/") //escape slashes
                            .replace("+", ".*") //replace wildcard with regex text
                            .toRegex()
                            .matches(topic) -> {
                            hotWordDetected(message, topic)
                        }
                        MQTTTopicsSubscription.IntentRecognitionResult.topic
                            .replace("/", "\\/") //escape slashes
                            .replace("+", ".*") //replace wildcard with regex text
                            .toRegex()
                            .matches(topic) -> {
                            intentRecognitionResult(message)
                        }
                    }
                }
            }

        } catch (e: Exception) {
            logger.e(e) { "onMessageReceived error" }
        }
    }

    private fun publishMessage(topic: String, message: MqttMessage) {

        coroutineScope.launch {
            message.msgId = id

            client?.publish(topic, message)?.also {
                logger.e { "unable to publish topic \n${it.statusCode.name} ${it.msg}" }
            }
        }

    }

    private fun JsonObject.isThisSiteId(): Boolean {
        val siteId = this["siteId"]?.jsonPrimitive?.content
        logger.v { "siteId is $siteId" }
        return siteId == ConfigurationSettings.siteId.value
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
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            StateMachine.startSession()
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
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            StateMachine.endSession(jsonObject["sessionId"]?.jsonPrimitive?.content)
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
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            jsonObject["sessionId"]?.jsonPrimitive?.content?.also {
                StateMachine.startedSession(it, "extern")
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
    fun sessionStarted(sessionId: String) {
        publishMessage(
            MQTTTopicsPublish.SessionStarted.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("sessionId", sessionId)
                    put("siteId", ConfigurationSettings.siteId.value)
                }).toByteArray()
            )
        )
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
    private fun sessionEnded(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            jsonObject["sessionId"]?.jsonPrimitive?.content?.also {
                StateMachine.sessionEnded(it)
            } ?: run {
                logger.d { "received sessionStarted with empty session Id" }
            }
        } else {
            logger.d { "received endSession but for other siteId" }
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
    fun sessionEnded(sessionId: String?) {
        publishMessage(
            MQTTTopicsPublish.SessionEnded.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("sessionId", sessionId)
                    put("siteId", ConfigurationSettings.siteId.value)
                }).toByteArray()
            )
        )
    }

    /**
     * hermes/dialogueManager/intentNotRecognized (JSON)
     *
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     */
    fun intentNotRecognized(sessionId: String?) {
        publishMessage(
            MQTTTopicsPublish.IntentNotRecognizedInSession.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("sessionId", sessionId)
                    put("siteId", ConfigurationSettings.siteId.value)
                }).toByteArray()
            )
        )
    }


    /**
     * Chunk of WAV audio data for site
     * wav_bytes: bytes - WAV data to play (message payload)
     * siteId: string - Hermes site ID (part of topic)
     */
    fun audioFrame(byteArray: List<Byte>) {
        publishMessage(
            MQTTTopicsPublish.AsrAudioFrame.topic.replace("<siteId>", ConfigurationSettings.siteId.value),
            MqttMessage(byteArray.toByteArray())
        )
    }

    /**
     * hermes/hotword/toggleOn (JSON)
     * Enables hotword detection
     * siteId: string = "default" - Hermes site ID
     * reason: string = "" - Reason for toggle on
     */
    private fun hotWordToggleOn(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            AppSettings.isHotWordEnabled.value = true
        } else {
            logger.d { "received hotWordToggleOn but for other siteId" }
        }
    }

    /**
     * hermes/hotword/toggleOff (JSON)
     * Disables hotword detection
     * siteId: string = "default" - Hermes site ID
     * reason: string = "" - Reason for toggle off
     */
    private fun hotWordToggleOff(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            AppSettings.isHotWordEnabled.value = false
        } else {
            logger.d { "received hotWordToggleOff but for other siteId" }
        }
    }


    /**
     * hermes/hotword/<wakewordId>/detected (JSON)
     * Indicates a hotword was successfully detected
     * wakewordId: string - wake word ID (part of topic)
     *
     * currentSensitivity: float = 1.0 - sensitivity of wake word detection (service specific)
     * siteId: string = "default" - Hermes site ID
     */
    private fun hotWordDetected(message: MqttMessage, topic: String) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        val hotWord = topic.replace("hermes/hotword/", "").replace("/detected", "")

        if (jsonObject.isThisSiteId()) {
            StateMachine.hotWordDetected(hotWord)
        } else {
            logger.d { "received hotWordToggleOff but for other siteId" }
        }
    }

    /**
     * hermes/hotword/<wakewordId>/detected (JSON)
     * Indicates a hotword was successfully detected
     * wakewordId: string - wake word ID (part of topic)
     *
     * currentSensitivity: float = 1.0 - sensitivity of wake word detection (service specific)
     * siteId: string = "default" - Hermes site ID
     */
    fun hotWordDetected() {
        publishMessage(
            MQTTTopicsPublish.HotWordDetected.topic.replace("<wakewordId>", "default"),
            MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("currentSensitivity", ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.value)
                    put("siteId", ConfigurationSettings.siteId.value)
                    //put("sendAudioCaptured", true)
                    //necessary
                    put(
                        "modelId",
                        "/usr/lib/rhasspy/.venv/lib/python3.7/site-packages/pvporcupine/resources/keyword_files/linux/jarvis_linux.ppn"
                    )
                }).toByteArray()
            )
        )
    }

    /**
     * hermes/error/hotword (JSON, Rhasspy only)
     * Sent when an error occurs in the hotword system
     * error: string - description of the error
     *
     * siteId: string = "default" - Hermes site ID
     */
    fun hotWordError(description: String) {
        publishMessage(
            MQTTTopicsPublish.HotWordError.topic,
            MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("error", description)
                    put("siteId", ConfigurationSettings.siteId.value)
                }).toByteArray()
            )
        )
    }

    /**
     * hermes/asr/startListening (JSON)
     * Tell ASR system to start recording/transcribing
     * siteId: string = "default" - Hermes site ID
     * sendAudioCaptured: bool = false - send audioCaptured after stop listening (Rhasspy only)
     * wakewordId: string? = null - id of wake word that triggered session (Rhasspy only)
     */
    private fun startListening(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            StateMachine.startListening(
                jsonObject["sessionId"]?.jsonPrimitive?.content,
                jsonObject["sendAudioCaptured"]?.jsonPrimitive?.booleanOrNull == true,
                true
            )
        } else {
            logger.d { "received startListening but for other siteId" }
        }
    }

    /**
     * hermes/asr/startListening (JSON)
     * Tell ASR system to start recording/transcribing
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     *
     * stopOnSilence: bool = true - detect silence and automatically end voice command (Rhasspy only)
     */
    fun startListening(sessionId: String?) {
        publishMessage(
            MQTTTopicsPublish.AsrStartListening.topic,
            MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("siteId", ConfigurationSettings.siteId.value)
                    put("sessionId", sessionId)
                    put("stopOnSilence", true)
                    put("sendAudioCaptured", true)
                }).toByteArray()
            )
        )
    }

    /**
     * hermes/asr/stopListening (JSON)
     * Tell ASR system to stop recording
     * Emits textCaptured if silence has was not detected earlier
     * siteId: string = "default" - Hermes site ID
     * sessionId: string = "" - current session ID
     */
    private fun stopListening(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            StateMachine.stopListening(jsonObject["sessionId"]?.jsonPrimitive?.content, true)
        } else {
            logger.d { "received stopListening but for other siteId" }
        }
    }

    /**
     * hermes/asr/stopListening (JSON)
     * Tell ASR system to stop recording
     * Emits textCaptured if silence has was not detected earlier
     * siteId: string = "default" - Hermes site ID
     * sessionId: string = "" - current session ID
     */
    fun stopListening(sessionId: String?) {
        publishMessage(
            MQTTTopicsPublish.AsrStopListening.topic,
            MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("siteId", ConfigurationSettings.siteId.value)
                    put("sessionId", sessionId)
                }).toByteArray()
            )
        )
    }

    /**
     * hermes/asr/textCaptured (JSON)
     * Successful transcription, sent either when silence is detected or on stopListening
     *
     * text: string - transcription text
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    private fun asrTextCaptured(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            StateMachine.intentTranscribed(
                jsonObject["sessionId"]?.jsonPrimitive?.content,
                jsonObject["text"]?.jsonPrimitive?.content ?: "",
                true
            )
        } else {
            logger.d { "received asrTextCaptured but for other siteId" }
        }
    }

    /**
     * hermes/asr/textCaptured (JSON)
     * Successful transcription, sent either when silence is detected or on stopListening
     *
     * text: string - transcription text
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    fun asrTextCaptured(sessionId: String?, text: String?) {
        publishMessage(
            MQTTTopicsPublish.AsrTextCaptured.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("text", JsonPrimitive(text))
                    put("siteId", ConfigurationSettings.siteId.value)
                    put("sessionId", sessionId)
                }).toByteArray()
            )
        )
    }

    /**
     * hermes/error/asr (JSON)
     * Sent when an error occurs in the ASR system
     *
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    private fun asrError(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            StateMachine.intentTranscriptionError(jsonObject["sessionId"]?.jsonPrimitive?.content, true)
        } else {
            logger.d { "received asrError but for other siteId" }
        }
    }


    /**
     * hermes/error/asr (JSON)
     * Sent when an error occurs in the ASR system
     *
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    fun asrError(sessionId: String?) {
        publishMessage(
            MQTTTopicsPublish.AsrError.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("siteId", ConfigurationSettings.siteId.value)
                    put("sessionId", sessionId)
                }).toByteArray()
            )
        )
    }

    /**
     * rhasspy/asr/<siteId>/<sessionId>/audioCaptured (binary, Rhasspy only)
     * WAV audio data captured by ASR session
     * siteId: string - Hermes site ID (part of topic)
     * sessionId: string - current session ID (part of topic)
     * Only sent if sendAudioCaptured = true in startListening
     */
    fun audioCaptured(sessionId: String?, byteData: List<Byte>) {
        publishMessage(
            MQTTTopicsPublish.AudioCaptured.topic
                .replace("<siteId>", ConfigurationSettings.siteId.value)
                .replace("<sessionId>", sessionId ?: ""),
            MqttMessage(byteData.toByteArray())
        )
    }

    /**
     * hermes/nlu/query (JSON)
     * Request an intent to be recognized from text
     * input: string - text to recognize intent from (required)
     *
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     *
     * Response(s)
     * hermes/intent/<intentName>
     * hermes/nlu/intentNotRecognized
     */
    fun intentQuery(sessionId: String?, text: String) {
        publishMessage(
            MQTTTopicsPublish.Query.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("input", JsonPrimitive(text))
                    put("siteId", ConfigurationSettings.siteId.value)
                    put("sessionId", sessionId)
                }).toByteArray()
            )
        )
    }

    /**
     * hermes/intent/<intentName> (JSON)
     * Sent when an intent was successfully recognized
     * input: string - text from query (required)
     * intent: object - details of recognized intent (required)
     *
     * siteId: string = "default" - Hermes site ID
     * sessionId: string = "" - current session ID
     *
     * Response to hermes/nlu/query
     */
    private fun intentRecognitionResult(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            val intent = jsonObject["intent"]?.jsonObject?.toString()
            intent?.also {
                StateMachine.intentRecognized(it, jsonObject["sessionId"]?.jsonPrimitive?.content)
            } ?: run {
                logger.d { "received intentRecognitionResult with empty intent" }
            }
        } else {
            logger.d { "received intentRecognitionResult but for other siteId" }
        }
    }


    /**
     * hermes/dialogueManager/intentNotRecognized (JSON)
     *
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     */
    private fun intentNotRecognized(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            StateMachine.intentNotRecognized(jsonObject["sessionId"]?.jsonPrimitive?.content)
        } else {
            logger.d { "received intentNotRecognized but for other siteId" }
        }
    }

    /**
     * hermes/handle/toggleOn
     * Enable intent handling
     */
    private fun intentHandlingToggleOn(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            AppSettings.isIntentHandlingEnabled.value = true
        } else {
            logger.d { "received intentHandlingToggleOn but for other siteId" }
        }
    }


    /**
     * hermes/handle/toggleOff
     * Disable intent handling
     */
    private fun intentHandlingToggleOff(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            AppSettings.isIntentHandlingEnabled.value = false
        } else {
            logger.d { "received intentHandlingToggleOff but for other siteId" }
        }
    }


    /**
     * hermes/tts/say (JSON)
     * Generate spoken audio for a sentence using the configured text to speech system
     * Automatically sends playBytes
     *
     * text: string - sentence to speak (required)
     *
     * volume: float? = null - volume level to speak with (0 = off, 1 = full volume)
     *
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     *
     * Response(s)
     * hermes/tts/sayFinished (JSON)
     */
    fun say(sessionId: String?, text: String) {
        publishMessage(
            MQTTTopicsPublish.Say.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("text", JsonPrimitive(text))
                    put("siteId", ConfigurationSettings.siteId.value)
                    put("sessionId", sessionId)
                }).toByteArray()
            )
        )
    }


    /**
     * hermes/audioServer/<siteId>/playBytes/<requestId> (JSON)
     * Play WAV data
     * wav_bytes: bytes - WAV data to play (message payload)
     * requestId: string - unique ID for request (part of topic)
     * siteId: string - Hermes site ID (part of topic)
     *
     * Response(s)
     * hermes/audioServer/<siteId>/playFinished (JSON)
     */
    private fun playBytes(message: MqttMessage) {
        //not necessary to check SiteId for this topic, because it is in topic string
        if (ConfigurationSettings.audioPlayingOption.value != AudioPlayingOptions.RemoteMQTT) {
            RhasspyActions.playAudio(message.payload.toList())
        } else {
            logger.d { "received playBytes but audio playing is set to mqtt, ignoring this to prevent looping" }
        }
    }

    /**
     * hermes/audioServer/<siteId>/playBytes/<requestId> (JSON)
     * Play WAV data
     * wav_bytes: bytes - WAV data to play (message payload)
     * requestId: string - unique ID for request (part of topic)
     * siteId: string - Hermes site ID (part of topic)
     *
     * Response(s)
     * hermes/audioServer/<siteId>/playFinished (JSON)
     */
    fun playBytes(data: List<Byte>) {
        publishMessage(
            MQTTTopicsPublish.AudioOutputPlayBytes.topic
                .replace("<siteId>", ConfigurationSettings.siteId.value)
                .replace("<requestId>", uuid4().toString()),
            MqttMessage(data.toByteArray())
        )
    }

    /**
     * hermes/audioServer/toggleOff (JSON)
     * Disable audio output
     * siteId: string = "default" - Hermes site ID
     */
    private fun audioOutputToggleOff(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            AppSettings.isAudioOutputEnabled.value = false
        } else {
            logger.d { "received audioOutputToggleOff but for other siteId" }
        }
    }

    /**
     * hermes/audioServer/toggleOn (JSON)
     * Enable audio output
     * siteId: string = "default" - Hermes site ID
     */
    private fun audioOutputToggleOn(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            AppSettings.isAudioOutputEnabled.value = true
        } else {
            logger.d { "received audioOutputToggleOff but for other siteId" }
        }
    }

    /**
     * hermes/audioServer/<siteId>/playFinished
     * Indicates that audio has finished playing
     * Response to hermes/audioServer/<siteId>/playBytes/<requestId>
     * siteId: string - Hermes site ID (part of topic)
     */
    fun playFinished() {
        publishMessage(
            MQTTTopicsPublish.AudioOutputPlayFinished.topic
                .replace("<siteId>", ConfigurationSettings.siteId.value),
            MqttMessage(ByteArray(0))
        )
    }

    /**
     * rhasspy/audioServer/setVolume (JSON, Rhasspy only)
     * Set the volume at one or more sitesu
     * volume: float - volume level to set (0 = off, 1 = full volume)
     * siteId: string = "default" - Hermes site ID
     */
    private fun setVolume(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            jsonObject["volume"]?.jsonPrimitive?.floatOrNull?.also {
                AppSettings.volume.value = it
            } ?: run {
                logger.e { "setVolume invalid value ${jsonObject["volume"]}" }
            }
        } else {
            logger.d { "received audioOutputToggleOff but for other siteId" }
        }
    }

}