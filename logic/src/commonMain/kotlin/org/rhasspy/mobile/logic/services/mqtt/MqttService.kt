package org.rhasspy.mobile.logic.services.mqtt

import com.benasher44.uuid.uuid4
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import okio.Path
import okio.buffer
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.SayText
import org.rhasspy.mobile.logic.middleware.Source.Mqtt
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.extensions.commonData
import org.rhasspy.mobile.platformspecific.extensions.commonSource
import org.rhasspy.mobile.platformspecific.mqtt.*
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.resources.MR
import kotlin.random.Random

interface IMqttService : IService {

    override val serviceState: StateFlow<ServiceState>
    val isConnected: StateFlow<Boolean>
    val isHasStarted: StateFlow<Boolean>

    fun onMessageReceived(topic: String, payload: ByteArray)

    fun sessionStarted(sessionId: String)
    fun sessionEnded(sessionId: String)
    fun intentNotRecognized(sessionId: String)
    fun asrAudioFrame(byteArray: ByteArray, onResult: (result: ServiceState) -> Unit)
    fun asrAudioSessionFrame(sessionId: String, byteArray: ByteArray, onResult: (result: ServiceState) -> Unit)
    fun hotWordDetected(keyword: String)
    fun wakeWordError(description: String)
    fun startListening(sessionId: String, onResult: (result: ServiceState) -> Unit)
    fun stopListening(sessionId: String, onResult: (result: ServiceState) -> Unit)
    fun asrTextCaptured(sessionId: String, text: String?)
    fun asrError(sessionId: String)
    fun audioCaptured(sessionId: String, audioFilePath: Path)
    fun recognizeIntent(sessionId: String, text: String, onResult: (result: ServiceState) -> Unit)
    fun say(sessionId: String, text: String, siteId: String, onResult: (result: ServiceState) -> Unit)
    fun playAudioRemote(audioSource: AudioSource, onResult: (result: ServiceState) -> Unit)
    fun playFinished()

}

internal class MqttService(
    paramsCreator: MqttServiceParamsCreator
) : IMqttService {

    override val logger = LogType.MqttService.logger()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    override val serviceState = _serviceState.readOnly

    private val nativeApplication by inject<NativeApplication>()
    private val serviceMiddleware by inject<IServiceMiddleware>()

    private var scope = CoroutineScope(Dispatchers.IO)

    private val paramsFlow: StateFlow<MqttServiceParams> = paramsCreator()
    private val params: MqttServiceParams get() = paramsFlow.value

    private val url get() = "tcp://${params.mqttHost}:${params.mqttPort}"

    private var client: MqttClient? = null
    private var retryJob: Job? = null
    private val id = Random.nextInt()

    private val _isConnected = MutableStateFlow(false)
    override val isConnected = _isConnected.readOnly
    private val _isHasStarted = MutableStateFlow(false)
    override val isHasStarted = _isHasStarted.readOnly

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
        scope.launch {
            paramsFlow.collect {
                stop()
                start()
            }
        }
    }

    private fun start() {
        if (params.isMqttEnabled) {
            logger.d { "initialize" }
            _serviceState.value = ServiceState.Loading

            try {
                client = buildClient()
                scope.launch {
                    try {
                        if (connectClient()) {
                            _serviceState.value = subscribeTopics()
                            _isHasStarted.value = true
                        } else {
                            _serviceState.value = ServiceState.Error(MR.strings.notConnected.stable)
                            logger.e { "client could not connect" }
                        }
                    } catch (exception: Exception) {
                        //start error
                        logger.e(exception) { "client connect error" }
                        _serviceState.value = ServiceState.Exception(exception)
                    }
                }
            } catch (exception: Exception) {
                //start error
                logger.e(exception) { "client initialization error" }
                _serviceState.value = ServiceState.Exception(exception)
            }
        } else {
            _serviceState.value = ServiceState.Disabled
        }
    }

    /**
     * stops client
     *
     * disconnects, resets connected value and deletes client object
     */
    private fun stop() {
        retryJob?.cancel()
        retryJob = null
        client?.disconnect()
        client = null
        _serviceState.value = ServiceState.Disabled
        _isHasStarted.value = false
        _isConnected.value = false
    }

    private fun buildClient(): MqttClient {
        logger.d { "buildClient" }
        return MqttClient(
            brokerUrl = url,
            clientId = params.siteId,
            persistenceType = MqttPersistence.MEMORY,
            onDelivered = { },
            onMessageReceived = { topic, message -> onMessageInternalReceived(topic, message) },
            onDisconnect = { error -> onDisconnect(error) },
        )
    }

    /**
     * connects client to server and returns if client is now connected
     */
    private suspend fun connectClient(): Boolean {
        logger.d { "connectClient" }
        client?.also {
            if (!it.isConnected.value) {
                //connect to server
                it.connect(params.mqttServiceConnectionOptions)?.also { error ->
                    logger.e { "connectClient error $error" }
                    _serviceState.value = MqttServiceStateType.fromMqttStatus(error.statusCode).serviceState
                }
            } else {
                _serviceState.value = ServiceState.Success
            }
            //update value, may be used from reconnect
            _isConnected.value = it.isConnected.value == true
        }

        return _isConnected.value
    }


    /**
     * try to reconnect after disconnect
     */
    private fun onDisconnect(throwable: Throwable) {
        logger.e(throwable) { "onDisconnect" }
        _isConnected.value = client?.isConnected?.value == true

        if (retryJob?.isActive != true) {
            retryJob = scope.launch {
                logger.e(throwable) { "start retryJob" }
                client?.also {
                    while (!it.isConnected.value) {
                        connectClient()
                        delay(params.retryInterval)
                    }
                    retryJob?.cancel()
                    retryJob = null
                }
            }
        }
    }

    private fun onMessageInternalReceived(topic: String, message: MqttMessage) {
        logger.d { "onMessageInternalReceived id ${message.msgId} $topic" }

        if (message.msgId == id) {
            //ignore all messages that i have send
            logger.d { "message ignored, was same id as send by myself" }
            return
        }

        onMessageReceived(topic, message.payload)
    }

    /**
     * checks topics that are equal, compared to enum name
     *
     * returns true when message was consumed
     */
    override fun onMessageReceived(topic: String, payload: ByteArray) {
        logger.d { "compareTopic $topic" }

        //topic matches enum
        getMqttTopic(topic)?.also { mqttTopic ->
            logger.d { "resulting topic $topic" }

            if (!mqttTopic.topic.contains(MqttTopicPlaceholder.SiteId.toString())) {
                //site id in payload
                //decode json object
                val jsonObject = Json.decodeFromString<JsonObject>(payload.decodeToString())
                //validate site id
                if (jsonObject.isThisSiteId()) {
                    when (mqttTopic) {
                        MqttTopicsSubscription.StartSession            -> startSession(jsonObject)
                        MqttTopicsSubscription.EndSession              -> endSession(jsonObject)
                        MqttTopicsSubscription.SessionStarted          -> sessionStarted(jsonObject)
                        MqttTopicsSubscription.SessionEnded            -> sessionEnded(jsonObject)
                        MqttTopicsSubscription.HotWordToggleOn         -> hotWordToggleOn()
                        MqttTopicsSubscription.HotWordToggleOff        -> hotWordToggleOff()
                        MqttTopicsSubscription.AsrStartListening       -> startListening(jsonObject)
                        MqttTopicsSubscription.AsrStopListening        -> stopListening(jsonObject)
                        MqttTopicsSubscription.AsrTextCaptured         -> asrTextCaptured(jsonObject)
                        MqttTopicsSubscription.AsrError                -> asrError(jsonObject)
                        MqttTopicsSubscription.IntentNotRecognized     -> intentNotRecognized(jsonObject)
                        MqttTopicsSubscription.IntentHandlingToggleOn  -> intentHandlingToggleOn()
                        MqttTopicsSubscription.IntentHandlingToggleOff -> intentHandlingToggleOff()
                        MqttTopicsSubscription.AudioOutputToggleOn     -> audioOutputToggleOn()
                        MqttTopicsSubscription.AudioOutputToggleOff    -> audioOutputToggleOff()
                        MqttTopicsSubscription.HotWordDetected         -> hotWordDetectedCalled(topic)
                        MqttTopicsSubscription.IntentRecognitionResult -> intentRecognitionResult(jsonObject)
                        MqttTopicsSubscription.SetVolume               -> setVolume(jsonObject)
                        MqttTopicsSubscription.Say                     -> say(jsonObject)
                        MqttTopicsSubscription.PlayBytes,
                        MqttTopicsSubscription.PlayFinished            -> {
                            logger.d { "isThisSiteId mqttTopic notFound $topic" }
                        }
                    }
                } else {
                    when (mqttTopic) {
                        MqttTopicsSubscription.AsrTextCaptured -> asrTextCaptured(jsonObject)
                        MqttTopicsSubscription.AsrError        -> asrError(jsonObject)
                        else                                   -> {
                            logger.d { "isNotThisSiteId mqttTopic notFound $topic $jsonObject" }
                        }
                    }
                }
            } else {
                when (mqttTopic) {
                    MqttTopicsSubscription.PlayBytes       -> playBytes(payload)
                    MqttTopicsSubscription.PlayFinished    -> playFinishedCall()
                    else                                   -> { logger.d { "isNotThisSiteId mqttTopic notFound $topic" } }
                }
            }
        } ?: run {

        }
    }


    /**
     * Subscribes to topics that are necessary
     */
    private suspend fun subscribeTopics(): ServiceState {
        logger.d { "subscribeTopics" }
        var hasError = false

        //subscribe to topics with this site id (if contained in topic, currently only in PlayBytes)
        MqttTopicsSubscription.values().forEach { mqttTopic ->
            try {
                client?.subscribe(mqttTopic.topic.set(MqttTopicPlaceholder.SiteId, params.siteId))
                    ?.also {
                        hasError = true
                    }
            } catch (exception: Exception) {
                hasError = true
                logger.e(exception) { "subscribeTopics error" }
            }
        }

        return if (hasError) {
            MqttServiceStateType.TopicSubscriptionFailed.serviceState
        } else {
            ServiceState.Success
        }
    }


    /**
     * published new messages
     *
     * boolean if message was published
     */
    private fun publishMessage(topic: String, message: MqttMessage, onResult: ((result: ServiceState) -> Unit)? = null) {
        scope.launch {
            val status = if (params.isMqttEnabled) {
                message.msgId = id

                client?.let { mqttClient ->
                    mqttClient.publish(topic, message)?.let {
                        logger.e { "mqtt publish error $it" }
                        MqttServiceStateType.fromMqttStatus(it.statusCode).serviceState
                    } ?: run {
                        logger.v { "$topic mqtt message published" }
                        ServiceState.Success
                    }
                } ?: run {
                    logger.a { "mqttClient not initialized" }
                    ServiceState.Exception()
                }

            } else {
                ServiceState.Success
            }
            _serviceState.value = status
            onResult?.invoke(status)
        }
    }

    private fun publishMessage(mqttTopic: MqttTopicsPublish, message: MqttMessage, onResult: ((result: ServiceState) -> Unit)? = null) {
        publishMessage(mqttTopic.topic, message, onResult)
    }

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
        serviceMiddleware.action(StartSession(jsonObject.getSource()))


    /**
     * https://rhasspy.readthedocs.io/en/latest/reference/#dialoguemanager_endsession
     *
     * hermes/dialogueManager/endSession (JSON)
     * Requests that a session be terminated nominally
     * sessionId: string - current session ID (required)
     */
    private fun endSession(jsonObject: JsonObject) =
        serviceMiddleware.action(EndSession(jsonObject.getSource()))

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
        serviceMiddleware.action(SessionStarted(jsonObject.getSource()))

    /**
     * hermes/dialogueManager/sessionStarted (JSON)
     * Indicates a session has started
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     *
     * Response to [hermes/dialogueManager/startSession]
     * Also used when session has started for other reasons
     */
    override fun sessionStarted(sessionId: String) {
        publishMessage(
            MqttTopicsPublish.SessionStarted,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
            }
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
    private fun sessionEnded(jsonObject: JsonObject) =
        serviceMiddleware.action(SessionEnded(jsonObject.getSource()))

    /**
     * hermes/dialogueManager/sessionEnded (JSON)
     * Indicates a session has terminated
     *
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     *
     * Response to hermes/dialogueManager/endSession or other reasons for a session termination
     */
    override fun sessionEnded(sessionId: String) {
        publishMessage(
            MqttTopicsPublish.SessionEnded,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
            }
        )
    }

    /**
     * hermes/dialogueManager/intentNotRecognized (JSON)
     *
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     */
    override fun intentNotRecognized(sessionId: String) {
        publishMessage(
            MqttTopicsPublish.IntentNotRecognizedInSession,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
            }
        )
    }

    /**
     * Chunk of WAV audio for site
     * wav_bytes: bytes - WAV data to play (message payload)
     * siteId: string - Hermes site ID (part of topic)
     */
    override fun asrAudioFrame(byteArray: ByteArray, onResult: (result: ServiceState) -> Unit) {
        publishMessage(
            MqttTopicsPublish.AsrAudioFrame.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId),
            MqttMessage(byteArray),
            onResult
        )
    }

    /**
     * Chunk of WAV audio data for session
     * wav_bytes: bytes - WAV data to play (message payload)
     * siteId: string - Hermes site ID (part of topic)
     * sessionId: string - session ID (part of topic)
     */
    override fun asrAudioSessionFrame(sessionId: String, byteArray: ByteArray, onResult: (result: ServiceState) -> Unit) {
        publishMessage(
            MqttTopicsPublish.AsrAudioSessionFrame.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId)
                .set(MqttTopicPlaceholder.SessionId, sessionId),
            MqttMessage(byteArray),
            onResult
        )
    }

    /**
     * hermes/hotword/toggleOn (JSON)
     * Enables hotword detection
     * siteId: string = "default" - Hermes site ID
     * reason: string = "" - Reason for toggle on
     */
    private fun hotWordToggleOn() =
        serviceMiddleware.action(HotWordToggle(true, Mqtt(null)))

    /**
     * hermes/hotword/toggleOff (JSON)
     * Disables hotword detection
     * siteId: string = "default" - Hermes site ID
     * reason: string = "" - Reason for toggle off
     */
    private fun hotWordToggleOff() =
        serviceMiddleware.action(HotWordToggle(false, Mqtt(null)))


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
                    serviceMiddleware.action(WakeWordDetected(Mqtt(null), it[2]))
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
     * modelId: string = "keyword" - Wake Word
     */
    override fun hotWordDetected(keyword: String) {
        publishMessage(
            MqttTopicsPublish.HotWordDetected.topic
                .set(MqttTopicPlaceholder.WakeWord, keyword),
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.ModelId, keyword)
            }
        )
    }

    /**
     * hermes/error/hotword (JSON, Rhasspy only)
     * Sent when an error occurs in the hotword system
     * error: string - description of the error
     *
     * siteId: string = "default" - Hermes site ID
     */
    override fun wakeWordError(description: String) {
        publishMessage(
            MqttTopicsPublish.WakeWordError,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.Error, description)
            }
        )
    }

    /**
     * hermes/asr/startListening (JSON)
     * Tell ASR system to start recording/transcribing
     * siteId: string = "default" - Hermes site ID
     * sendAudioCaptured: bool = false - send audioCaptured after stop listening (Rhasspy only)
     * wakewordId: string? = null - id of wake word that triggered session (Rhasspy only)
     */
    private fun startListening(jsonObject: JsonObject) =
        serviceMiddleware.action(
            StartListening(
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
    override fun startListening(sessionId: String, onResult: (result: ServiceState) -> Unit) {
        publishMessage(
            MqttTopicsPublish.AsrStartListening,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
                put(MqttParams.StopOnSilence, params.isUseSpeechToTextMqttSilenceDetection)
                put(MqttParams.SendAudioCaptured, false)
            },
            onResult
        )
    }


    /**
     * hermes/asr/stopListening (JSON)
     * Tell ASR system to stop recording
     * Emits textCaptured if silence has was not detected earlier
     * siteId: string = "default" - Hermes site ID
     * sessionId: string = "" - current session ID
     */
    private fun stopListening(jsonObject: JsonObject) =
        serviceMiddleware.action(StopListening(jsonObject.getSource()))

    /**
     * hermes/asr/stopListening (JSON)
     * Tell ASR system to stop recording
     * Emits textCaptured if silence has was not detected earlier
     * siteId: string = "default" - Hermes site ID
     * sessionId: string = "" - current session ID
     */
    override fun stopListening(sessionId: String, onResult: (result: ServiceState) -> Unit) {
        publishMessage(
            MqttTopicsPublish.AsrStopListening,
            createMqttMessage {
                put(MqttParams.SessionId, sessionId)
            },
            onResult
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
    private fun asrTextCaptured(jsonObject: JsonObject) =
        serviceMiddleware.action(
            AsrTextCaptured(
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
    override fun asrTextCaptured(sessionId: String, text: String?) {
        publishMessage(
            MqttTopicsPublish.AsrTextCaptured,
            createMqttMessage {
                put(MqttParams.Text, JsonPrimitive(text))
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
            }
        )
    }

    /**
     * hermes/error/asr (JSON)
     * Sent when an error occurs in the ASR system
     *
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    private fun asrError(jsonObject: JsonObject) =
        serviceMiddleware.action(AsrError(Mqtt(jsonObject.getSessionId())))


    /**
     * hermes/error/asr (JSON)
     * Sent when an error occurs in the ASR system
     *
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    override fun asrError(sessionId: String) {
        publishMessage(
            MqttTopicsPublish.AsrError,
            createMqttMessage {
                put(MqttParams.SessionId, sessionId)
            }
        )
    }

    /**
     * rhasspy/asr/<siteId>/<sessionId>/audioCaptured (binary, Rhasspy only)
     * WAV audio data captured by ASR session
     * siteId: string - Hermes site ID (part of topic)
     * sessionId: string - current session ID (part of topic)
     * Only sent if sendAudioCaptured = true in startListening
     */
    override fun audioCaptured(sessionId: String, audioFilePath: Path) {
        with(audioFilePath.commonSource().buffer()) {
            publishMessage(
                MqttTopicsPublish.AudioCaptured.topic
                    .set(MqttTopicPlaceholder.SiteId, params.siteId)
                    .set(MqttTopicPlaceholder.SessionId, sessionId),
                MqttMessage(this.readByteArray())
            )
            this.close()
        }
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
    override fun recognizeIntent(sessionId: String, text: String, onResult: (result: ServiceState) -> Unit) {
        publishMessage(
            MqttTopicsPublish.Query,
            createMqttMessage {
                put(MqttParams.Input, JsonPrimitive(text))
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
            },
            onResult
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
    private fun intentRecognitionResult(jsonObject: JsonObject) =
        serviceMiddleware.action(
            IntentRecognitionResult(
                source = jsonObject.getSource(),
                intentName = jsonObject[MqttParams.Intent.value]?.jsonObject?.get(MqttParams.IntentName.value)?.jsonPrimitive?.content
                    ?: "",
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
        serviceMiddleware.action(IntentRecognitionError(jsonObject.getSource()))

    /**
     * hermes/handle/toggleOn
     * Enable intent handling
     */
    private fun intentHandlingToggleOn() =
        serviceMiddleware.action(IntentHandlingToggle(true, Mqtt(null)))

    /**
     * hermes/handle/toggleOff
     * Disable intent handling
     */
    private fun intentHandlingToggleOff() =
        serviceMiddleware.action(IntentHandlingToggle(false, Mqtt(null)))

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
    override fun say(sessionId: String, text: String, siteId: String, onResult: (result: ServiceState) -> Unit) {
        publishMessage(
            MqttTopicsPublish.Say,
            createMqttMessage {
                put(MqttParams.SiteId, siteId)
                put(MqttParams.SessionId, sessionId)
                put(MqttParams.Text, JsonPrimitive(text))
            },
            onResult
        )
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
    private fun say(jsonObject: JsonObject) =
        jsonObject[MqttParams.Text.value]?.jsonPrimitive?.content?.let {
            serviceMiddleware.action(
                SayText(
                    text = it,
                    volume = jsonObject[MqttParams.Volume.value]?.jsonPrimitive?.floatOrNull,
                    siteId = jsonObject.getSiteId() ?: "default",
                    sessionId = jsonObject.getSessionId()
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
    private fun playBytes(byteArray: ByteArray) {
        serviceMiddleware.action(PlayAudio(Mqtt(null), byteArray))
    }

    /**
     * hermes/audioServer/<siteId>/playFinished
     * Indicates that audio has finished playing
     * Response to hermes/audioServer/<siteId>/playBytes/<requestId>
     * siteId: string - Hermes site ID (part of topic)
     * id: string = "" - requestId from request message
     */
    private fun playFinishedCall() =
        serviceMiddleware.action(PlayFinished(Mqtt(null)))

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
    override fun playAudioRemote(audioSource: AudioSource, onResult: (result: ServiceState) -> Unit) {
        publishMessage(
            MqttTopicsPublish.AudioOutputPlayBytes.topic
                .set(MqttTopicPlaceholder.SiteId, params.audioPlayingMqttSiteId)
                .set(MqttTopicPlaceholder.RequestId, uuid4().toString()),
            MqttMessage(
                @Suppress("DEPRECATION")
                when (audioSource) {
                    is AudioSource.Data     -> audioSource.data
                    is AudioSource.File     -> audioSource.path.commonSource().buffer().readByteArray()
                    is AudioSource.Resource -> audioSource.fileResource.commonData(nativeApplication)
                }
            ),
            onResult
        )
    }

    /**
     * hermes/audioServer/toggleOn (JSON)
     * Enable audio output
     * siteId: string = "default" - Hermes site ID
     */
    private fun audioOutputToggleOn() =
        serviceMiddleware.action(AudioOutputToggle(true, Mqtt(null)))

    /**
     * hermes/audioServer/toggleOff (JSON)
     * Disable audio output
     * siteId: string = "default" - Hermes site ID
     */
    private fun audioOutputToggleOff() =
        serviceMiddleware.action(AudioOutputToggle(false, Mqtt(null)))

    /**
     * hermes/audioServer/<siteId>/playFinished
     * Indicates that audio has finished playing
     * Response to hermes/audioServer/<siteId>/playBytes/<requestId>
     * siteId: string - Hermes site ID (part of topic)
     */
    override fun playFinished() {
        publishMessage(
            MqttTopicsPublish.AudioOutputPlayFinished.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId),
            MqttMessage(ByteArray(0))
        )
    }


    /**
     * rhasspy/audioServer/setVolume (JSON, Rhasspy only)
     * Set the volume at one or more sitesu
     * volume: float - volume level to set (0 = off, 1 = full volume)
     * siteId: string = "default" - Hermes site ID
     */
    private fun setVolume(jsonObject: JsonObject) =
        jsonObject[MqttParams.Volume.value]?.jsonPrimitive?.floatOrNull?.let {
            serviceMiddleware.action(AudioVolumeChange(it, Mqtt(null)))
        }


    private fun JsonObject.getSource() = Mqtt(jsonObject.getSessionId())

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
        println("match topic $this with $regex")
        return this
            .replace("/", "\\/") //escape slashes
            .replace("+", ".*") //replace wildcard with regex text
            .toRegex()
            .matches(regex)
    }

    private fun getMqttTopic(topic: String): MqttTopicsSubscription? {
        return when {
            MqttTopicsSubscription.HotWordDetected.topic.matches(topic)         -> MqttTopicsSubscription.HotWordDetected
            MqttTopicsSubscription.IntentRecognitionResult.topic.matches(topic) -> MqttTopicsSubscription.IntentRecognitionResult
            MqttTopicsSubscription.PlayBytes.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId)
                .matches(topic)                                                 -> MqttTopicsSubscription.PlayBytes

            MqttTopicsSubscription.PlayFinished.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId)
                .matches(topic)                                                 -> MqttTopicsSubscription.PlayFinished

            else                                                                -> MqttTopicsSubscription.fromTopic(topic)
        }
    }

}