package org.rhasspy.mobile.logic.services.mqtt

import MQTTClient
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import mqtt.Subscription
import mqtt.packets.Qos
import mqtt.packets.mqtt.MQTTPublish
import mqtt.packets.mqttv5.ReasonCode
import okio.Path
import okio.buffer
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
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
import org.rhasspy.mobile.platformspecific.readOnly
import kotlin.random.Random
import kotlin.random.nextUInt

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

    private var client: MQTTClient? = null
    private var retryJob: Job? = null
    private val id = Random.nextUInt()

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
                        _serviceState.value = subscribeTopics()
                        client?.run()
                        _isHasStarted.value = true
                        _isConnected.value = true
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
        try {
            client?.disconnect(ReasonCode.UNSPECIFIED_ERROR)
            client = null
        } catch (e: Exception) {
            //UnresolvedAddressException
        }
        _serviceState.value = ServiceState.Disabled
        _isHasStarted.value = false
        _isConnected.value = false
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun buildClient(): MQTTClient {
        logger.d { "buildClient" }
        return MQTTClient(
            mqttVersion = 4,
            address = params.mqttHost,
            port = params.mqttPort,
            tls = null,
            keepAlive = params.retryInterval.toInt(), //TODO keep alive interval
            webSocket = false,
            cleanStart = params.mqttServiceConnectionOptions.cleanStart,
            clientId = params.siteId,
            userName = params.mqttServiceConnectionOptions.connUsername,
            password = params.mqttServiceConnectionOptions.connPassword.encodeToByteArray().toUByteArray(),
            publishReceived = { message ->
                onMessageInternalReceived(message)
            }
        )
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun onMessageInternalReceived(message: MQTTPublish) {
        logger.d { "onMessageInternalReceived id ${message.packetId} ${message.topicName}" }

        if (message.packetId == id) {
            //ignore all messages that i have send
            logger.d { "message ignored, was same id as send by myself" }
            return
        }

        message.payload?.also {
            onMessageReceived(message.topicName, it.toByteArray())
        }

    }

    /**
     * message from external sources
     * eg: http api
     */
    override fun onMessageReceived(topic: String, payload: ByteArray) {
        logger.d { "onMessageReceived $topic" }

        try {
            //regex topic
            if (!regexTopic(topic, payload)) {
                compareTopic(topic, payload)
            } else {
                logger.d { "regexTopic matched $topic" }
            }

        } catch (e: Exception) {
            logger.e(e) { "received message on $topic error" }
        }
    }


    /**
     * consumes messages that match by regex
     *
     * returns true when message was consumed
     */
    private fun regexTopic(topic: String, payload: ByteArray): Boolean {
        logger.d { "regexTopic $topic" }
        when {
            MqttTopicsSubscription.HotWordDetected.topic.matches(topic)         -> {
                hotWordDetectedCalled(topic)
            }

            MqttTopicsSubscription.IntentRecognitionResult.topic.matches(topic) -> {
                val jsonObject = Json.decodeFromString<JsonObject>(payload.decodeToString())
                intentRecognitionResult(jsonObject)
            }

            MqttTopicsSubscription.PlayBytes.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId)
                .matches(topic)                                                 -> {
                playBytes(payload)
            }

            else                                                                -> return false
        }
        return true
    }

    /**
     * checks topics that are equal, compared to enum name
     *
     * returns true when message was consumed
     */
    private fun compareTopic(topic: String, payload: ByteArray) {
        logger.d { "compareTopic $topic" }

        //topic matches enum
        getMqttTopic(topic)?.also { mqttTopic ->

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
            }
        } ?: run {
            //site id in topic
            when {
                MqttTopicsSubscription.PlayBytes.topic
                    .set(MqttTopicPlaceholder.SiteId, params.siteId)
                    .matches(topic) -> {
                    playBytes(payload)
                }

                MqttTopicsSubscription.PlayFinished.topic
                    .set(MqttTopicPlaceholder.SiteId, params.siteId)
                    .matches(topic) -> {
                    playFinishedCall()
                }

                else                -> {
                    logger.d { "siteId in Topic mqttTopic notFound $topic" }
                }
            }
        }
    }


    /**
     * Subscribes to topics that are necessary
     */
    private fun subscribeTopics(): ServiceState {
        logger.d { "subscribeTopics" }
        var hasError = false

        val topics = MqttTopicsSubscription.values().map { Subscription(topicFilter = it.topic.set(MqttTopicPlaceholder.SiteId, params.siteId)) }
        //subscribe to topics with this site id (if contained in topic, currently only in PlayBytes)
        try {
            client?.subscribe(topics)
        } catch (exception: Exception) {
            hasError = true
            logger.e(exception) { "subscribeTopics error" }
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
    /* private fun publishMessage(topic: String, message: MQTTPublish, onResult: ((result: ServiceState) -> Unit)? = null) {
         scope.launch {
             val status = if (params.isMqttEnabled) {
                 message.packetId = id

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
     }*/

    private fun publishMessage(
        mqttTopic: MqttTopicsPublish,
        builderAction: JsonObjectBuilder.() -> Unit,
        onResult: ((result: ServiceState) -> Unit)? = null
    ) {
        publishMessage(
            mqttTopic = mqttTopic.topic,
            builderAction = builderAction,
        )
    }

    private fun publishMessage(
        mqttTopic: String,
        builderAction: JsonObjectBuilder.() -> Unit,
        onResult: ((result: ServiceState) -> Unit)? = null
    ) {
        publishMessage(
            mqttTopic = mqttTopic,
            data = Json.encodeToString(buildJsonObject(builderAction)).encodeToByteArray(),
        )
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun publishMessage(
        mqttTopic: String,
        data: ByteArray,
        onResult: ((result: ServiceState) -> Unit)? = null
    ) {
        scope.launch {
            try {
                client?.publish(
                    retain = false,
                    qos = Qos.EXACTLY_ONCE,
                    topic = mqttTopic,
                    payload = data.toUByteArray(),
                )
            } catch (e: Exception) {
                logger.e(e) { "publishMessage" }
            }
        }
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
            mqttTopic = MqttTopicsPublish.SessionStarted,
            builderAction = {
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
            mqttTopic = MqttTopicsPublish.SessionEnded,
            builderAction = {
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
            mqttTopic = MqttTopicsPublish.IntentNotRecognizedInSession,
            builderAction = {
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
            mqttTopic = MqttTopicsPublish.AsrAudioFrame.topic.set(MqttTopicPlaceholder.SiteId, params.siteId),
            data = byteArray,
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
            mqttTopic = MqttTopicsPublish.AsrAudioSessionFrame.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId)
                .set(MqttTopicPlaceholder.SessionId, sessionId),
            data = byteArray,
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
            mqttTopic = MqttTopicsPublish.HotWordDetected.topic.set(MqttTopicPlaceholder.WakeWord, keyword),
            builderAction = {
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
            mqttTopic = MqttTopicsPublish.WakeWordError,
            builderAction = {
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
            mqttTopic = MqttTopicsPublish.AsrStartListening,
            builderAction = {
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
            mqttTopic = MqttTopicsPublish.AsrStopListening,
            builderAction = {
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
            mqttTopic = MqttTopicsPublish.AsrTextCaptured,
            builderAction = {
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
            mqttTopic = MqttTopicsPublish.AsrError,
            builderAction = {
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
                mqttTopic = MqttTopicsPublish.AudioCaptured.topic
                    .set(MqttTopicPlaceholder.SiteId, params.siteId)
                    .set(MqttTopicPlaceholder.SessionId, sessionId),
                data = this.readByteArray()
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
            mqttTopic = MqttTopicsPublish.Query,
            builderAction = {
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
                intentName = jsonObject[MqttParams.Intent.value]?.jsonObject?.get(MqttParams.IntentName.value)?.jsonPrimitive?.content ?: "",
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
            mqttTopic = MqttTopicsPublish.Say,
            builderAction = {
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
            mqttTopic = MqttTopicsPublish.AudioOutputPlayBytes.topic
                .set(MqttTopicPlaceholder.SiteId, params.audioPlayingMqttSiteId)
                .set(MqttTopicPlaceholder.RequestId, uuid4().toString()),
            data = when (audioSource) {
                is AudioSource.Data -> audioSource.data
                is AudioSource.File -> audioSource.path.commonSource().buffer().readByteArray()
                is AudioSource.Resource -> audioSource.fileResource.commonData(nativeApplication)
            },
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
            mqttTopic = MqttTopicsPublish.AudioOutputPlayFinished.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId),
            data = ByteArray(0)
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
                .matches(topic)                                                 -> MqttTopicsSubscription.IntentRecognitionResult

            else                                                                -> MqttTopicsSubscription.fromTopic(
                topic
            )
        }
    }

}