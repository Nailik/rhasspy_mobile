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
import org.rhasspy.mobile.middleware.ErrorType.MqttServiceErrorType.*
import org.rhasspy.mobile.middleware.EventType.MqttServiceEventType.*
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.mqtt.*
import org.rhasspy.mobile.nativeutils.MqttClient
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.ServiceResponse
import org.rhasspy.mobile.middleware.Action.AppSettingsAction
import org.rhasspy.mobile.middleware.Action.DialogAction
import org.rhasspy.mobile.middleware.Source

class MqttService : IService() {

    private val params by inject<MqttServiceParams>()

    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val logger = Logger.withTag("MqttService")

    private var scope = CoroutineScope(Dispatchers.Default)
    private val url = "tcp://${params.mqttHost}:${params.mqttPort}"

    private var client: MqttClient? = null
    private var retryJob: Job? = null
    private val id = uuid4().variant

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.readOnly
    private val _isHasStarted = MutableStateFlow(false)
    val isHasStarted = _isHasStarted.readOnly


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
        if (params.isMqttEnabled) {
            val startEvent = serviceMiddleware.createEvent(Start)

            try {
                client = buildClient()
                scope.launch {
                    if (connectClient()) {
                        startEvent.success()

                        subscribeTopics()
                        _isHasStarted.value = true
                    } else {
                        startEvent.error()
                    }
                }

            } catch (exception: Exception) {
                //start error
                startEvent.error(exception)
            }
        } else {
            logger.v { "mqtt not enabled" }
        }
    }

    private fun buildClient(): MqttClient {
        return MqttClient(
            brokerUrl = url,
            clientId = params.siteId,
            persistenceType = MqttPersistence.MEMORY,
            onDelivered = { },
            onMessageReceived = { topic, message ->
                scope.launch {
                    onMessageReceived(topic, message)
                }
            },
            onDisconnect = { error -> onDisconnect(error) },
        )
    }

    /**
     * stops client
     *
     * disconnects, resets connected value and deletes client object
     */
    override fun onClose() {
        _isHasStarted.value = false
        _isConnected.value = false
        client?.disconnect()
        retryJob?.cancel()
        retryJob = null
        scope.cancel()
    }

    /**
     * connects client to server and returns if client is now connected
     */
    private suspend fun connectClient(): Boolean {
        val connectEvent = serviceMiddleware.createEvent(Connecting)

        client?.also {
            if (!it.isConnected) {
                //connect to server
                it.connect(params.mqttServiceConnectionOptions)?.also { error ->
                    connectEvent.error(ConnectionError(error))
                } ?: run {
                    connectEvent.success()
                }
            } else {
                connectEvent.error(AlreadyConnected)
            }
            //update value, may be used from reconnect
            _isConnected.value = it.isConnected == true
        } ?: run {
            connectEvent.error(NotInitialized)
        }

        return _isConnected.value
    }


    /**
     * try to reconnect after disconnect
     */
    private fun onDisconnect(error: Throwable) {
        serviceMiddleware.createEvent(Disconnect).error(error)


        _isConnected.value = client?.isConnected == true

        if (retryJob?.isActive != true) {
            retryJob = scope.launch {

                val reconnectEvent = serviceMiddleware.createEvent(Reconnect)

                client?.also {
                    while (!it.isConnected) {
                        connectClient()
                        delay(params.retryInterval)
                    }
                    reconnectEvent.success()

                    retryJob?.cancel()
                    retryJob = null
                }
            }
        }
    }


    private fun getMqttTopic(topic: String): MqttTopicsSubscription? {
        return when {
            MqttTopicsSubscription.HotWordDetected.topic.matches(topic) -> MqttTopicsSubscription.HotWordDetected
            MqttTopicsSubscription.IntentRecognitionResult.topic.matches(topic) -> MqttTopicsSubscription.IntentRecognitionResult
            MqttTopicsSubscription.PlayBytes.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId)
                .matches(topic) -> MqttTopicsSubscription.IntentRecognitionResult
            else -> MqttTopicsSubscription.fromTopic(topic)
        }
    }

    private fun onMessageReceived(topic: String, message: MqttMessage) {
        logger.v { "onMessageReceived id ${message.msgId} $topic" }

        if (message.msgId == id) {
            //ignore all messages that i have send
            logger.v { "message ignored, was same id as send by myself" }
            return
        }

        //TODO session id to be checked by dialog manager
        //TODO events when should be ignored not nice (like wrong site id but used for AsrTextCaptured duo to session id)
        val receivedEvent = serviceMiddleware.createEvent(Received, topic)

        try {
            //regex topic
            when {
                MqttTopicsSubscription.HotWordDetected.topic.matches(topic) -> {
                    hotWordDetectedCalled(topic)
                    receivedEvent.success()
                    return
                }
                MqttTopicsSubscription.IntentRecognitionResult.topic.matches(topic) -> {
                    val jsonObject =
                        Json.decodeFromString<JsonObject>(message.payload.decodeToString())
                    intentRecognitionResult(jsonObject)
                    receivedEvent.success()
                    return
                }
                MqttTopicsSubscription.PlayBytes.topic
                    .set(MqttTopicPlaceholder.SiteId, params.siteId)
                    .matches(topic) -> {
                    playBytes(message.payload)
                    receivedEvent.success()
                    return
                }
            }

            //topic matches enum
            getMqttTopic(topic)?.also { mqttTopic ->

                if (!mqttTopic.topic.contains(MqttTopicPlaceholder.SiteId.toString())) {
                    //site id in payload
                    //decode json object
                    val jsonObject =
                        Json.decodeFromString<JsonObject>(message.payload.decodeToString())
                    //validate site id
                    if (jsonObject.isThisSiteId()) {
                        when (mqttTopic) {
                            MqttTopicsSubscription.StartSession -> startSession(jsonObject)
                            MqttTopicsSubscription.EndSession -> endSession(jsonObject)
                            MqttTopicsSubscription.SessionStarted -> sessionStarted(jsonObject)
                            MqttTopicsSubscription.SessionEnded -> sessionEnded(jsonObject)
                            MqttTopicsSubscription.HotWordToggleOn -> hotWordToggleOn()
                            MqttTopicsSubscription.HotWordToggleOff -> hotWordToggleOff()
                            MqttTopicsSubscription.AsrStartListening -> startListening(jsonObject)
                            MqttTopicsSubscription.AsrStopListening -> stopListening(jsonObject)
                            MqttTopicsSubscription.AsrTextCaptured -> asrTextCaptured(jsonObject)
                            MqttTopicsSubscription.AsrError -> asrError(jsonObject)
                            MqttTopicsSubscription.IntentNotRecognized -> intentNotRecognized(
                                jsonObject
                            )
                            MqttTopicsSubscription.IntentHandlingToggleOn -> intentHandlingToggleOn()
                            MqttTopicsSubscription.IntentHandlingToggleOff -> intentHandlingToggleOff()
                            MqttTopicsSubscription.AudioOutputToggleOff -> audioOutputToggleOff()
                            MqttTopicsSubscription.AudioOutputToggleOn -> audioOutputToggleOn()
                            MqttTopicsSubscription.HotWordDetected -> hotWordDetectedCalled(topic)
                            MqttTopicsSubscription.IntentRecognitionResult -> intentRecognitionResult(
                                jsonObject
                            )
                            MqttTopicsSubscription.SetVolume -> if (!setVolume(jsonObject)) {
                                receivedEvent.error(InvalidVolume)
                            }
                            else -> receivedEvent.error(InvalidTopic)
                        }
                        receivedEvent.success(jsonObject.toString())
                    } else {
                        when (mqttTopic) {
                            MqttTopicsSubscription.AsrTextCaptured -> asrTextCaptured(jsonObject)
                            MqttTopicsSubscription.AsrError -> asrError(jsonObject)
                            else -> {
                                logger.v { "message ignored, different side id $jsonObject" }
                                receivedEvent.warning()
                            }
                        }
                    }
                } else {
                    //site id in topic
                    when {
                        MqttTopicsSubscription.PlayBytes.topic
                            .set(MqttTopicPlaceholder.SiteId, params.siteId)
                            .matches(topic) -> {
                            playBytes(message.payload)
                            receivedEvent.success()
                        }
                        else -> receivedEvent.error(InvalidTopic)
                    }
                }
            } ?: run {
                //no topic found
                receivedEvent.warning()
            }
        } catch (e: Exception) {
            receivedEvent.error(e)
        }
    }

    /**
     * Subscribes to topics that are necessary
     */
    private suspend fun subscribeTopics() {
        val subscribeTopicsEvent = serviceMiddleware.createEvent(SubscribeTopic)

        var hasError = false
        var hasSuccess = false

        //subscribe to topics with this site id (if contained in topic, currently only in PlayBytes)
        MqttTopicsSubscription.values().forEach { mqttTopic ->
            val subscribeEvent = serviceMiddleware.createEvent(Subscribing, mqttTopic.topic)

            try {
                client?.subscribe(mqttTopic.topic.set(MqttTopicPlaceholder.SiteId, params.siteId))
                    ?.also {
                        hasError = true
                        subscribeEvent.error(SubscriptionError(it))
                    } ?: run {
                    hasSuccess = true
                    subscribeEvent.success()
                }
            } catch (e: Exception) {
                subscribeEvent.error(e)
            }
        }

        if (hasError) {
            if (hasSuccess) {
                //some worked
                subscribeTopicsEvent.warning()
            } else {
                //none worked
                subscribeTopicsEvent.error()
            }
        } else {
            subscribeTopicsEvent.success()
        }
    }


    /**
     * published new messages
     */
    private suspend fun publishMessage(topic: String, message: MqttMessage): ServiceResponse<*> {
        val publishEvent = serviceMiddleware.createEvent(Publish)

        message.msgId = id

        return client?.let { mqttClient ->
            mqttClient.publish(topic, message)?.let {
                publishEvent.error(PublishError(it))
                ServiceResponse.Error(Exception(it.statusCode.name))
            } ?: run {
                publishEvent.success()
                ServiceResponse.Success(Unit)
            }
        } ?: run {
            publishEvent.error(NotInitialized)
            ServiceResponse.NotInitialized
        }
    }

    private suspend fun publishMessage(
        mqttTopic: MqttTopicsPublish,
        message: MqttMessage
    ): ServiceResponse<*> =
        publishMessage(mqttTopic.topic, message)

    /**
     * create a new message
     */
    private fun createMqttMessage(builderAction: JsonObjectBuilder.() -> Unit): MqttMessage =
        MqttMessage(Json.encodeToString(buildJsonObject(builderAction)).toByteArray())

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
    private fun startSession(jsonObject: JsonObject) =
        serviceMiddleware.action(DialogAction.StartSession(jsonObject.getSource()))


    /**
     * https://rhasspy.readthedocs.io/en/latest/reference/#dialoguemanager_endsession
     *
     * hermes/dialogueManager/endSession (JSON)
     * Requests that a session be terminated nominally
     * sessionId: string - current session ID (required)
     */
    private fun endSession(jsonObject: JsonObject) =
        serviceMiddleware.action(DialogAction.EndSession(jsonObject.getSource()))

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
    private fun sessionStarted(jsonObject: JsonObject) =
        serviceMiddleware.action(DialogAction.SessionStarted(jsonObject.getSource()))

    /**
     * hermes/dialogueManager/sessionStarted (JSON)
     * Indicates a session has started
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     *
     * Response to [hermes/dialogueManager/startSession]
     * Also used when session has started for other reasons
     */
    suspend fun sessionStarted(sessionId: String): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.SessionStarted,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
            }
        )

    /**
     * hermes/dialogueManager/sessionEnded (JSON)
     * Indicates a session has terminated
     *
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     *
     * Response to hermes/dialogueManager/endSession or other reasons for a session termination
     */
    private fun sessionEnded(jsonObject: JsonObject) =
        serviceMiddleware.action(DialogAction.SessionEnded(jsonObject.getSource()))

    /**
     * hermes/dialogueManager/sessionEnded (JSON)
     * Indicates a session has terminated
     *
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     *
     * Response to hermes/dialogueManager/endSession or other reasons for a session termination
     */
    suspend fun sessionEnded(sessionId: String): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.SessionEnded,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
            }
        )

    /**
     * hermes/dialogueManager/intentNotRecognized (JSON)
     *
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     */
    suspend fun intentNotRecognized(sessionId: String): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.IntentNotRecognizedInSession,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
            }
        )

    /**
     * Chunk of WAV audio data for site
     * wav_bytes: bytes - WAV data to play (message payload)
     * siteId: string - Hermes site ID (part of topic)
     */
    suspend fun audioFrame(byteArray: List<Byte>): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.AsrAudioFrame.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId),
            MqttMessage(byteArray.toByteArray())
        )

    /**
     * hermes/hotword/toggleOn (JSON)
     * Enables hotword detection
     * siteId: string = "default" - Hermes site ID
     * reason: string = "" - Reason for toggle on
     */
    private fun hotWordToggleOn() =
        serviceMiddleware.action(AppSettingsAction.HotWordToggle(true))

    /**
     * hermes/hotword/toggleOff (JSON)
     * Disables hotword detection
     * siteId: string = "default" - Hermes site ID
     * reason: string = "" - Reason for toggle off
     */
    private fun hotWordToggleOff() =
        serviceMiddleware.action(AppSettingsAction.HotWordToggle(false))


    /**
     * hermes/hotword/<wakewordId>/detected (JSON)
     * Indicates a hotword was successfully detected
     * wakewordId: string - wake word ID (part of topic)
     *
     * currentSensitivity: float = 1.0 - sensitivity of wake word detection (service specific)
     * siteId: string = "default" - Hermes site ID
     */
    private fun hotWordDetectedCalled(topic: String): Boolean =
        topic.split("/").let {
            if (it.size > 2) {
                scope.launch {
                    serviceMiddleware.action(DialogAction.HotWordDetected(Source.Mqtt(null), it[2]))
                }
                true
            } else {
                false
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
    suspend fun hotWordDetected(keyword: String): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.HotWordDetected.topic
                .set(MqttTopicPlaceholder.WakeWord, keyword),
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.ModelId, keyword)
            }
        )

    /**
     * hermes/error/hotword (JSON, Rhasspy only)
     * Sent when an error occurs in the hotword system
     * error: string - description of the error
     *
     * siteId: string = "default" - Hermes site ID
     */
    suspend fun hotWordError(description: String): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.HotWordError,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.Error, description)
            }
        )

    /**
     * hermes/asr/startListening (JSON)
     * Tell ASR system to start recording/transcribing
     * siteId: string = "default" - Hermes site ID
     * sendAudioCaptured: bool = false - send audioCaptured after stop listening (Rhasspy only)
     * wakewordId: string? = null - id of wake word that triggered session (Rhasspy only)
     */
    private fun startListening(jsonObject: JsonObject) =
        serviceMiddleware.action(
            DialogAction.StartListening(
                jsonObject.getSource(),
                jsonObject[MqttParams.SendAudioCaptured.value]?.jsonPrimitive?.booleanOrNull == true
            )
        )

    /**
     * hermes/asr/startListening (JSON)
     * Tell ASR system to start recording/transcribing
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     *
     * stopOnSilence: bool = true - detect silence and automatically end voice command (Rhasspy only)
     */
    suspend fun startListening(sessionId: String): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.AsrStartListening,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
                put(MqttParams.StopOnSilence, params.isUseSpeechToTextMqttSilenceDetection)
                put(MqttParams.SendAudioCaptured, true)
            }
        )


    /**
     * hermes/asr/stopListening (JSON)
     * Tell ASR system to stop recording
     * Emits textCaptured if silence has was not detected earlier
     * siteId: string = "default" - Hermes site ID
     * sessionId: string = "" - current session ID
     */
    private fun stopListening(jsonObject: JsonObject) =
        serviceMiddleware.action(DialogAction.StopListening(jsonObject.getSource()))

    /**
     * hermes/asr/stopListening (JSON)
     * Tell ASR system to stop recording
     * Emits textCaptured if silence has was not detected earlier
     * siteId: string = "default" - Hermes site ID
     * sessionId: string = "" - current session ID
     */
    suspend fun stopListening(sessionId: String): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.AsrStopListening,
            createMqttMessage {
                put(MqttParams.SessionId, sessionId)
            }
        )

    /**
     * hermes/asr/textCaptured (JSON)
     * Successful transcription, sent either when silence is detected or on stopListening
     *
     * text: string - transcription text
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    private fun asrTextCaptured(jsonObject: JsonObject) =
        serviceMiddleware.action(
            DialogAction.AsrTextCaptured(
                jsonObject.getSource(),
                jsonObject[MqttParams.Text.value]?.jsonPrimitive?.content
            )
        )

    /**
     * hermes/asr/textCaptured (JSON)
     * Successful transcription, sent either when silence is detected or on stopListening
     *
     * text: string - transcription text
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    suspend fun asrTextCaptured(sessionId: String, text: String?): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.AsrTextCaptured,
            createMqttMessage {
                put(MqttParams.Text, JsonPrimitive(text))
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
            }
        )

    /**
     * hermes/error/asr (JSON)
     * Sent when an error occurs in the ASR system
     *
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    private fun asrError(jsonObject: JsonObject) =
        serviceMiddleware.action(DialogAction.AsrError(Source.Mqtt(jsonObject.getSessionId())))


    /**
     * hermes/error/asr (JSON)
     * Sent when an error occurs in the ASR system
     *
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    suspend fun asrError(sessionId: String): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.AsrError,
            createMqttMessage {
                put(MqttParams.SessionId, sessionId)
            }
        )

    /**
     * rhasspy/asr/<siteId>/<sessionId>/audioCaptured (binary, Rhasspy only)
     * WAV audio data captured by ASR session
     * siteId: string - Hermes site ID (part of topic)
     * sessionId: string - current session ID (part of topic)
     * Only sent if sendAudioCaptured = true in startListening
     */
    suspend fun audioCaptured(sessionId: String, byteData: List<Byte>) {
        publishMessage(
            MqttTopicsPublish.AudioCaptured.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId)
                .set(MqttTopicPlaceholder.SessionId, sessionId),
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
    suspend fun recognizeIntent(sessionId: String, text: String): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.Query,
            createMqttMessage {
                put(MqttParams.Input, JsonPrimitive(text))
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
            }
        )

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
    private fun intentRecognitionResult(jsonObject: JsonObject) =
        serviceMiddleware.action(
            DialogAction.IntentRecognitionResult(
                source = jsonObject.getSource(),
                intentName = jsonObject[MqttParams.Intent.value]?.jsonObject?.get(MqttParams.IntentName.value)?.jsonPrimitive?.content,
                intent = jsonObject.toString()
            )
        )

    /**
     * hermes/dialogueManager/intentNotRecognized (JSON)
     *
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     */
    private fun intentNotRecognized(jsonObject: JsonObject) =
        serviceMiddleware.action(DialogAction.IntentTranscribedError(jsonObject.getSource()))

    /**
     * hermes/handle/toggleOn
     * Enable intent handling
     */
    private fun intentHandlingToggleOn() =
        serviceMiddleware.action(AppSettingsAction.IntentHandlingToggle(true))

    /**
     * hermes/handle/toggleOff
     * Disable intent handling
     */
    private fun intentHandlingToggleOff() =
        serviceMiddleware.action(AppSettingsAction.IntentHandlingToggle(false))

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
    suspend fun say(sessionId: String, text: String): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.Say,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
                put(MqttParams.Text, JsonPrimitive(text))
            }
        )


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
    private fun playBytes(payload: ByteArray) =
        serviceMiddleware.action(DialogAction.PlayAudio(Source.Mqtt(null), payload))

    /**
     * hermes/audioServer/<siteId>/playBytes/<requestId> (JSON)
     * Play WAV data
     * wav_bytes: bytes - WAV data to play (message payload)
     * requestId: string - unique ID for request (part of topic)
     * siteId: string - Hermes site ID (part of topic)
     *
     * Response(s)
     * hermes/audioServer/<siteId>/playFinished (JSON)
     *
     */
    suspend fun playBytes(data: List<Byte>): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.AudioOutputPlayBytes.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId)
                .set(MqttTopicPlaceholder.RequestId, uuid4().toString()),
            MqttMessage(data.toByteArray())
        )

    /**
     * hermes/audioServer/toggleOff (JSON)
     * Disable audio output
     * siteId: string = "default" - Hermes site ID
     */
    private fun audioOutputToggleOff() =
        serviceMiddleware.action(AppSettingsAction.AudioOutputToggle(false))

    /**
     * hermes/audioServer/toggleOn (JSON)
     * Enable audio output
     * siteId: string = "default" - Hermes site ID
     */
    private fun audioOutputToggleOn() =
        serviceMiddleware.action(AppSettingsAction.AudioOutputToggle(true))

    /**
     * hermes/audioServer/<siteId>/playFinished
     * Indicates that audio has finished playing
     * Response to hermes/audioServer/<siteId>/playBytes/<requestId>
     * siteId: string - Hermes site ID (part of topic)
     */
    suspend fun playFinished(): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.AudioOutputPlayFinished.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId),
            MqttMessage(ByteArray(0))
        )


    /**
     * rhasspy/audioServer/setVolume (JSON, Rhasspy only)
     * Set the volume at one or more sitesu
     * volume: float - volume level to set (0 = off, 1 = full volume)
     * siteId: string = "default" - Hermes site ID
     */
    private fun setVolume(jsonObject: JsonObject): Boolean =
        jsonObject[MqttParams.Volume.value]?.jsonPrimitive?.floatOrNull?.let {
            serviceMiddleware.action(AppSettingsAction.AudioVolumeChange(it))
            true
        } ?: false


    private fun JsonObject.getSource() = Source.Mqtt(jsonObject.getSessionId())

    /**
     * check if site id is this id
     */
    private fun JsonObject.isThisSiteId(): Boolean =
        this.getSiteId() == params.siteId

    private fun JsonObject.getSessionId(): String? =
        this[MqttParams.SessionId.value]?.jsonPrimitive?.content

    private fun JsonObject.getSiteId(): String? =
        this[MqttParams.SiteId.value]?.jsonPrimitive?.content

    private fun JsonObjectBuilder.put(key: MqttParams, element: Boolean): JsonElement? =
        put(key.value, element)

    private fun JsonObjectBuilder.put(key: MqttParams, element: JsonElement): JsonElement? =
        put(key.value, element)

    private fun JsonObjectBuilder.put(key: MqttParams, value: String?): JsonElement? =
        put(key.value, value)

    private fun String.set(key: MqttTopicPlaceholder, value: String): String =
        this.replace(key.placeholder, value)

    private fun String.matches(regex: String): Boolean {
        return this
            .replace("/", "\\/") //escape slashes
            .replace("+", ".*") //replace wildcard with regex text
            .toRegex()
            .matches(regex)
    }

}