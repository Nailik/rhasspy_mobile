package org.rhasspy.mobile.services.mqtt

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.benasher44.uuid.variant
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.koin.core.component.inject
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.mqtt.*
import org.rhasspy.mobile.nativeutils.MqttClient
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.ServiceResponse
import org.rhasspy.mobile.services.statemachine.StateMachineService
import kotlin.math.min

class MqttService : IService() {
    private val logger = Logger.withTag("MqttService")

    private var client: MqttClient? = null
    private val id = uuid4().variant
    private var scope = CoroutineScope(Dispatchers.Default)
    private var retryJob: Job? = null

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.readOnly

    private val params by inject<MqttServiceParams>()
    private val stateMachineService by inject<StateMachineService>()

    /**
     * start client externally, only starts if mqtt is enabled
     *
     * creates new client
     * connects client to server
     *
     * subscribes to topics necessary if connection was successful
     *
     * sets connected value
     */
    init {
        scope = CoroutineScope(Dispatchers.Default)
        if (params.isMqttEnabled) {
            client = MqttClient(
                brokerUrl = "tcp://${params.mqttHost}:${params.mqttPort}",
                clientId = params.siteId,
                persistenceType = MqttPersistence.MEMORY,
                onDelivered = { },
                onMessageReceived = { topic, message ->
                    scope.launch {
                        onMessageReceived(topic, message)
                    }
                },
                onDisconnect = { error -> onDisconnect(error) },
            ).also {
                scope.launch {
                    if (connectClient(it)) {
                        subscribeTopics()
                    } else {
                        logger.e { "client not connected after attempt" }
                    }
                }
            }
        } else {
            logger.v { "mqtt not enabled" }
        }
    }

    /**
     * stops client
     *
     * disconnects, resets connected value and deletes client object
     */
    override fun onClose() {
        client?.disconnect()
        _isConnected.value = false
        retryJob?.cancel()
        retryJob = null
        scope.cancel()
    }

    /**
     * connects client to server and returns if client is now connected
     */
    private suspend fun connectClient(client: MqttClient): Boolean {
        if (!client.isConnected) {
            //only if not connected
            logger.v { "connectClient" }
            //connect to server
            client.connect(params.mqttServiceConnectionOptions)?.also {
                logger.e { "connect \n${it.statusCode.name} ${it.msg}" }
                stateMachineService.mqttServiceError(it)
            } ?: run {
                stateMachineService.mqttServiceStartedSuccessfully()
            }
        } else {
            logger.v { "connectClient already connected" }
        }

        _isConnected.value = client.isConnected
        return _isConnected.value
    }


    /**
     * try to reconnect after disconnect
     */
    private fun onDisconnect(error: Throwable) {
        logger.e(error) { "onDisconnect" }
        _isConnected.value = client?.isConnected == true

        if (retryJob?.isActive != true) {
            retryJob = scope.launch {
                client?.also {
                    while (!it.isConnected) {
                        connectClient(it)
                        delay(params.retryInterval)
                    }
                    retryJob?.cancel()
                    retryJob = null
                }
            }
        }
    }

    private suspend fun onMessageReceived(topic: String, message: MqttMessage) {
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
                            .replace("<siteId>", params.siteId)
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

    /**
     * Subscribes to topics that are necessary
     */
    private suspend fun subscribeTopics() {
        logger.v { "subscribeTopics" }

        //subscribe to topics with this site id (if contained in topic, currently only in PlayBytes)
        MQTTTopicsSubscription.values().forEach { topic ->
            client?.subscribe(
                topic.topic.replace("<siteId>", params.siteId)
            )?.also {
                logger.e { "subscribe $topic \n${it.statusCode.name} ${it.msg}" }
            }
        }
    }

    /**
     * published new messages
     */
    private suspend fun publishMessage(topic: String, message: MqttMessage): ServiceResponse<*> {
        message.msgId = id

        return client?.let { mqttClient ->
            mqttClient.publish(topic, message)?.let {
                logger.e { "unable to publish topic \n${it.statusCode.name} ${it.msg}" }
                ServiceResponse.Error(Exception(it.statusCode.name))
            } ?: ServiceResponse.Success(Unit)
        } ?: ServiceResponse.NotInitialized()

    }

    /**
     * check if site id is this id
     */
    private fun JsonObject.isThisSiteId(): Boolean {
        val siteId = this["siteId"]?.jsonPrimitive?.content
        logger.v { "siteId is $siteId" }
        return siteId == params.siteId
    }

    private fun JsonObject.getSessionId(): String? {
        return jsonObject["sessionId"]?.jsonPrimitive?.content
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
    private suspend fun startSession(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            stateMachineService.startMqttSession()
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
            stateMachineService.endMqttSession(jsonObject.getSessionId())
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
            stateMachineService.startedMqttSession(jsonObject.getSessionId())
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
    suspend fun sessionStarted(sessionId: String): ServiceResponse<*> {
        return publishMessage(
            MQTTTopicsPublish.SessionStarted.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("sessionId", sessionId)
                    put("siteId", params.siteId)
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
            stateMachineService.sessionEndedMqtt(jsonObject.getSessionId())
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
    suspend fun sessionEnded(sessionId: String?): ServiceResponse<*> {
        return publishMessage(
            MQTTTopicsPublish.SessionEnded.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("sessionId", sessionId)
                    put("siteId", params.siteId)
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
    suspend fun intentNotRecognized(sessionId: String?): ServiceResponse<*> {
        return publishMessage(
            MQTTTopicsPublish.IntentNotRecognizedInSession.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("sessionId", sessionId)
                    put("siteId", params.siteId)
                }).toByteArray()
            )
        )
    }


    /**
     * Chunk of WAV audio data for site
     * wav_bytes: bytes - WAV data to play (message payload)
     * siteId: string - Hermes site ID (part of topic)
     */
    suspend fun audioFrame(byteArray: List<Byte>): ServiceResponse<*> {
        return publishMessage(
            MQTTTopicsPublish.AsrAudioFrame.topic.replace("<siteId>", params.siteId),
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
            stateMachineService.toggleHotWordEnabledMqtt(true)
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
            stateMachineService.toggleHotWordEnabledMqtt(false)
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
    private suspend fun hotWordDetected(message: MqttMessage, topic: String) {
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
    suspend fun hotWordDetected(keyword: String) {
        publishMessage(
            MQTTTopicsPublish.HotWordDetected.topic.replace("<wakewordId>", keyword),
            MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    //put("currentSensitivity", ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.value)
                    put("siteId", params.siteId)
                    //put("sendAudioCaptured", true)
                    //necessary
                    put("modelId", keyword)
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
    suspend fun hotWordError(description: String) {
        publishMessage(
            MQTTTopicsPublish.HotWordError.topic,
            MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("error", description)
                    put("siteId", params.siteId)
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
    private suspend fun startListening(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            stateMachineService.startListeningMqtt(
                jsonObject.getSessionId(),
                jsonObject["sendAudioCaptured"]?.jsonPrimitive?.booleanOrNull == true
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
    suspend fun startListening(sessionId: String?) {
        publishMessage(
            MQTTTopicsPublish.AsrStartListening.topic,
            MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("siteId", params.siteId)
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
    private suspend fun stopListening(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            stateMachineService.stopListeningMqtt(jsonObject.getSessionId())
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
    suspend fun stopListening(sessionId: String?) {
        publishMessage(
            MQTTTopicsPublish.AsrStopListening.topic,
            MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("siteId", params.siteId)
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
            stateMachineService.intentTranscribedMqtt(jsonObject.getSessionId(), jsonObject["text"]?.jsonPrimitive?.content)
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
    suspend fun asrTextCaptured(sessionId: String?, text: String?) {
        publishMessage(
            MQTTTopicsPublish.AsrTextCaptured.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("text", JsonPrimitive(text))
                    put("siteId", params.siteId)
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
            stateMachineService.intentTranscribedErrorMqtt(jsonObject.getSessionId())
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
    suspend fun asrError(sessionId: String?) {
        publishMessage(
            MQTTTopicsPublish.AsrError.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("siteId", params.siteId)
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
    suspend fun audioCaptured(sessionId: String?, byteData: List<Byte>) {
        publishMessage(
            MQTTTopicsPublish.AudioCaptured.topic
                .replace("<siteId>", params.siteId)
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
    suspend fun recognizeIntent(text: String, sessionId: String? = StateMachine.currentSession.sessionId): ServiceResponse<*> {
        return publishMessage(
            MQTTTopicsPublish.Query.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("input", JsonPrimitive(text))
                    put("siteId", params.siteId)
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
    private suspend fun intentRecognitionResult(message: MqttMessage) {
        val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())

        if (jsonObject.isThisSiteId()) {
            val intent = jsonObject.toString()
            val intentName = jsonObject["intent"]?.jsonObject?.get("intentName")?.jsonPrimitive?.content
            val sessionId = jsonObject.getSessionId()

            stateMachineService.intentRecognizedMqtt(sessionId, intentName, intent)
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
            stateMachineService.intentNotRecognizedMqtt(jsonObject.getSessionId())
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
            stateMachineService.toggleIntentHandlingEnabledMqtt(true)
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
            stateMachineService.toggleIntentHandlingEnabledMqtt(false)
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
    suspend fun say(text: String, sessionId: String? = StateMachine.currentSession.sessionId): ServiceResponse<*> {
        return publishMessage(
            MQTTTopicsPublish.Say.topic, MqttMessage(
                payload = Json.encodeToString(buildJsonObject {
                    put("text", JsonPrimitive(text))
                    put("siteId", params.siteId)
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
        stateMachineService.playAudioMqtt(message.payload.toList())
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
    suspend fun playBytes(data: List<Byte>): ServiceResponse<*> {
        return publishMessage(
            MQTTTopicsPublish.AudioOutputPlayBytes.topic
                .replace("<siteId>", params.siteId)
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
            stateMachineService.toggleAudioOutputEnabledMqtt(false)
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
            stateMachineService.toggleAudioOutputEnabledMqtt(true)
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
    suspend fun playFinished() {
        publishMessage(
            MQTTTopicsPublish.AudioOutputPlayFinished.topic.replace("<siteId>", params.siteId),
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
                stateMachineService.setAudioVolumeMqtt(it)
            } ?: run {
                logger.e { "setVolume invalid value ${jsonObject["volume"]}" }
            }
        } else {
            logger.d { "received audioOutputToggleOff but for other siteId" }
        }
    }

}