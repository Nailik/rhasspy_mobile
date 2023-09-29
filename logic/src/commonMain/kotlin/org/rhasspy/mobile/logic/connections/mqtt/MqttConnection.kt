package org.rhasspy.mobile.logic.connections.mqtt

import co.touchlab.kermit.Logger
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.mqtt.MqttServiceConnectionOptions
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.logic.connections.IConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.*
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult.AsrError
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult.AsrTextCaptured
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentNotRecognized
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentRecognitionResult
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult.PlayBytes
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult.PlayFinished
import org.rhasspy.mobile.logic.local.settings.IAppSettingsUtil
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.appendWavHeader
import org.rhasspy.mobile.platformspecific.extensions.commonData
import org.rhasspy.mobile.platformspecific.extensions.commonSource
import org.rhasspy.mobile.platformspecific.mqtt.*
import org.rhasspy.mobile.platformspecific.readOnly
import kotlin.random.Random

interface IMqttConnection : IConnection {

    val isConnected: StateFlow<Boolean>
    val isHasStarted: StateFlow<Boolean>

    val incomingMessages: Flow<MqttConnectionEvent>

    fun onMessageReceived(topic: String, payload: ByteArray)

    fun sessionStarted(sessionId: String)
    fun sessionEnded(sessionId: String)
    fun intentNotRecognized(sessionId: String)
    suspend fun asrAudioFrame(
        data: ByteArray,
        sampleRate: AudioFormatSampleRateType,
        encoding: AudioFormatEncodingType,
        channel: AudioFormatChannelType,
    ): MqttResult

    suspend fun asrAudioSessionFrame(
        sessionId: String,
        sampleRate: AudioFormatSampleRateType,
        encoding: AudioFormatEncodingType,
        channel: AudioFormatChannelType,
        data: ByteArray,
    ): MqttResult

    suspend fun hotWordDetected(keyword: String): MqttResult
    fun wakeWordError(description: String)
    suspend fun startListening(sessionId: String, isUseSilenceDetection: Boolean): MqttResult
    suspend fun stopListening(sessionId: String): MqttResult
    fun asrTextCaptured(sessionId: String, text: String?)
    fun asrError(sessionId: String)
    fun audioCaptured(sessionId: String, audioFilePath: Path)
    suspend fun recognizeIntent(sessionId: String, text: String): MqttResult
    suspend fun say(sessionId: String, text: String, volume: Float?, siteId: String, id: String): MqttResult
    suspend fun playAudioRemote(audioSource: AudioSource, siteId: String, id: String): MqttResult
    fun playFinished()

}

internal class MqttConnection(
    private val appSettingsUtil: IAppSettingsUtil,
    paramsCreator: MqttConnectionParamsCreator
) : IMqttConnection {

    private val logger = Logger.withTag("MqttConnection")

    override val connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Pending)

    override val incomingMessages = MutableSharedFlow<MqttConnectionEvent>()

    private val nativeApplication by inject<NativeApplication>()

    private var scope = CoroutineScope(Dispatchers.IO)

    private val paramsFlow: StateFlow<MqttConnectionParams> = paramsCreator()
    private val params: MqttConnectionParams get() = paramsFlow.value

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
        if (params.mqttConnectionData.isEnabled) {
            logger.d { "initialize" }
            connectionState.value = ConnectionState.Loading

            try {
                client = buildClient()
                scope.launch {
                    try {
                        if (connectClient()) {
                            connectionState.value = subscribeTopics()
                            _isHasStarted.value = true
                        }
                    } catch (exception: Exception) {
                        //start error
                        logger.e(exception) { "client connect error" }
                        connectionState.value = ConnectionState.ErrorState.Exception(exception)
                    }
                }
            } catch (exception: Exception) {
                //start error
                logger.e(exception) { "client initialization error" }
                connectionState.value = ConnectionState.ErrorState.Exception(exception)
            }
        } else {
            connectionState.value = ConnectionState.Disabled
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
        connectionState.value = ConnectionState.Disabled
        _isHasStarted.value = false
        _isConnected.value = false
    }

    private fun buildClient(): MqttClient {
        logger.d { "buildClient" }
        return MqttClient(
            brokerUrl = params.mqttConnectionData.host,
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
                it.connect(
                    MqttServiceConnectionOptions(
                        isSSLEnabled = params.mqttConnectionData.isSSLEnabled,
                        keyStorePath = params.mqttConnectionData.keystoreFile?.toPath(),
                        connectionTimeout = params.mqttConnectionData.connectionTimeout,
                        keepAliveInterval = params.mqttConnectionData.keepAliveInterval,
                        connUsername = params.mqttConnectionData.userName,
                        connPassword = params.mqttConnectionData.password,
                    )
                )?.also { error ->
                    logger.e { "connectClient error $error" }
                    connectionState.value = MqttConnectionStateType.fromMqttStatus(error.statusCode).connectionState
                }
            } else {
                connectionState.value = ConnectionState.Success
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
                        delay(params.mqttConnectionData.retryInterval)
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
                    MqttTopicsSubscription.PlayBytes    -> playBytes(payload, mqttTopic.topic.substringAfterLast("/"))
                    MqttTopicsSubscription.PlayFinished -> {
                        val jsonObject = Json.decodeFromString<JsonObject>(payload.decodeToString())
                        playFinishedCall(jsonObject.getId() ?: "")
                    }

                    else                                -> {
                        logger.d { "isNotThisSiteId mqttTopic notFound $topic" }
                    }
                }
            }
        }
    }


    /**
     * Subscribes to topics that are necessary
     */
    private suspend fun subscribeTopics(): ConnectionState {
        logger.d { "subscribeTopics" }
        var hasError = false

        //subscribe to topics with this site id (if contained in topic, currently only in PlayBytes)
        MqttTopicsSubscription.entries.forEach { mqttTopic ->
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
            MqttConnectionStateType.TopicSubscriptionFailed.connectionState
        } else {
            ConnectionState.Success
        }
    }


    /**
     * published new messages
     *
     * boolean if message was published
     */
    private suspend fun publishMessage(topic: String, message: MqttMessage): MqttResult {
        return try {
            if (params.mqttConnectionData.isSSLEnabled) {
                message.msgId = id

                client?.let { mqttClient ->
                    mqttClient.publish(topic, message)?.let {
                        logger.e { "mqtt publish error $it" }
                        MqttConnectionStateType.fromMqttStatus(it.statusCode).connectionState
                    } ?: run {
                        logger.v { "$topic mqtt message published" }
                        ConnectionState.Success
                    }
                } ?: run {
                    logger.a { "mqttClient not initialized" }
                    ConnectionState.ErrorState.Exception()
                }

            } else {
                ConnectionState.Success
            }
        } catch (exception: Exception) {
            ConnectionState.ErrorState.Exception(exception)
        }.let {
            connectionState.value = it

            if (it == ConnectionState.Success) MqttResult.Success else MqttResult.Error
        }
    }

    private suspend fun publishMessage(mqttTopic: MqttTopicsPublish, message: MqttMessage): MqttResult {
        return publishMessage(mqttTopic.topic, message)
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
        incomingMessages.tryEmit(StartSession(jsonObject.getSessionId()))


    /**
     * https://rhasspy.readthedocs.io/en/latest/reference/#dialoguemanager_endsession
     *
     * hermes/dialogueManager/endSession (JSON)
     * Requests that a session be terminated nominally
     * sessionId: string - current session ID (required)
     */
    private fun endSession(jsonObject: JsonObject) =
        incomingMessages.tryEmit(EndSession(jsonObject.getSessionId(), jsonObject[MqttParams.Text.value]?.jsonPrimitive?.content))

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
        incomingMessages.tryEmit(SessionStarted(jsonObject.getSessionId()))

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
        scope.launch {
            publishMessage(
                MqttTopicsPublish.SessionStarted,
                createMqttMessage {
                    put(MqttParams.SiteId, params.siteId)
                    put(MqttParams.SessionId, sessionId)
                }
            )
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
    private fun sessionEnded(jsonObject: JsonObject) =
        incomingMessages.tryEmit(SessionEnded(jsonObject.getSessionId()))

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
        scope.launch {
            publishMessage(
                MqttTopicsPublish.SessionEnded,
                createMqttMessage {
                    put(MqttParams.SiteId, params.siteId)
                    put(MqttParams.SessionId, sessionId)
                }
            )
        }
    }

    /**
     * hermes/dialogueManager/intentNotRecognized (JSON)
     *
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     */
    override fun intentNotRecognized(sessionId: String) {
        scope.launch {
            publishMessage(
                MqttTopicsPublish.IntentNotRecognizedInSession,
                createMqttMessage {
                    put(MqttParams.SiteId, params.siteId)
                    put(MqttParams.SessionId, sessionId)
                }
            )
        }
    }

    /**
     * Chunk of WAV audio for site
     * wav_bytes: bytes - WAV data to play (message payload)
     * siteId: string - Hermes site ID (part of topic)
     */
    override suspend fun asrAudioFrame(
        data: ByteArray,
        sampleRate: AudioFormatSampleRateType,
        encoding: AudioFormatEncodingType,
        channel: AudioFormatChannelType,
    ): MqttResult {
        val dataToSend = data.appendWavHeader(
            sampleRate = sampleRate.value,
            bitRate = encoding.bitRate,
            channel = channel.value,
        )

        return publishMessage(
            topic = MqttTopicsPublish.AsrAudioFrame.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId),
            message = MqttMessage(dataToSend),
        )
    }

    /**
     * Chunk of WAV audio data for session
     * wav_bytes: bytes - WAV data to play (message payload)
     * siteId: string - Hermes site ID (part of topic)
     * sessionId: string - session ID (part of topic)
     */
    override suspend fun asrAudioSessionFrame(
        sessionId: String,
        sampleRate: AudioFormatSampleRateType,
        encoding: AudioFormatEncodingType,
        channel: AudioFormatChannelType,
        data: ByteArray,
    ): MqttResult {
        val dataToSend = data.appendWavHeader(
            sampleRate = sampleRate.value,
            bitRate = encoding.bitRate,
            channel = channel.value,
        )

        return publishMessage(
            topic = MqttTopicsPublish.AsrAudioSessionFrame.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId)
                .set(MqttTopicPlaceholder.SessionId, sessionId),
            message = MqttMessage(dataToSend),
        )
    }

    /**
     * hermes/hotword/toggleOn (JSON)
     * Enables hotword detection
     * siteId: string = "default" - Hermes site ID
     * reason: string = "" - Reason for toggle on
     */
    private fun hotWordToggleOn() = appSettingsUtil.hotWordToggle(true, Source.Mqtt)
    // serviceMiddleware.action(HotWordToggle(true, Mqtt(null)))

    /**
     * hermes/hotword/toggleOff (JSON)
     * Disables hotword detection
     * siteId: string = "default" - Hermes site ID
     * reason: string = "" - Reason for toggle off
     */
    private fun hotWordToggleOff() = appSettingsUtil.hotWordToggle(false, Source.Mqtt)


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
                incomingMessages.tryEmit(HotWordDetected(it[2]))
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
    override suspend fun hotWordDetected(keyword: String): MqttResult =
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
    override fun wakeWordError(description: String) {
        scope.launch {
            publishMessage(
                MqttTopicsPublish.WakeWordError,
                createMqttMessage {
                    put(MqttParams.SiteId, params.siteId)
                    put(MqttParams.Error, description)
                }
            )
        }
    }

    /**
     * hermes/asr/startListening (JSON)
     * Tell ASR system to start recording/transcribing
     * siteId: string = "default" - Hermes site ID
     * sendAudioCaptured: bool = false - send audioCaptured after stop listening (Rhasspy only)
     * wakewordId: string? = null - id of wake word that triggered session (Rhasspy only)
     * sessionId: string? = null - current session ID
     */
    private fun startListening(jsonObject: JsonObject) =
        incomingMessages.tryEmit(
            StartListening(
                sessionId = jsonObject.getSessionId(),
                sendAudioCaptured = jsonObject[MqttParams.SendAudioCaptured.value]?.jsonPrimitive?.booleanOrNull == true
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
    override suspend fun startListening(sessionId: String, isUseSilenceDetection: Boolean): MqttResult {
        return publishMessage(
            MqttTopicsPublish.AsrStartListening,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
                put(MqttParams.StopOnSilence, isUseSilenceDetection)
                put(MqttParams.SendAudioCaptured, false)
            },
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
        incomingMessages.tryEmit(StopListening(jsonObject.getSessionId()))

    /**
     * hermes/asr/stopListening (JSON)
     * Tell ASR system to stop recording
     * Emits textCaptured if silence has was not detected earlier
     * siteId: string = "default" - Hermes site ID
     * sessionId: string = "" - current session ID
     */
    override suspend fun stopListening(sessionId: String): MqttResult {
        return publishMessage(
            MqttTopicsPublish.AsrStopListening,
            createMqttMessage {
                put(MqttParams.SessionId, sessionId)
            },
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
        incomingMessages.tryEmit(
            AsrTextCaptured(
                sessionId = jsonObject.getSessionId(),
                text = jsonObject[MqttParams.Text.value]?.jsonPrimitive?.content
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
        scope.launch {
            publishMessage(
                MqttTopicsPublish.AsrTextCaptured,
                createMqttMessage {
                    put(MqttParams.Text, JsonPrimitive(text))
                    put(MqttParams.SiteId, params.siteId)
                    put(MqttParams.SessionId, sessionId)
                }
            )
        }
    }

    /**
     * hermes/error/asr (JSON)
     * Sent when an error occurs in the ASR system
     *
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    private fun asrError(jsonObject: JsonObject) =
        incomingMessages.tryEmit(AsrError(jsonObject.getSessionId()))


    /**
     * hermes/error/asr (JSON)
     * Sent when an error occurs in the ASR system
     *
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    override fun asrError(sessionId: String) {
        scope.launch {
            publishMessage(
                MqttTopicsPublish.AsrError,
                createMqttMessage {
                    put(MqttParams.SessionId, sessionId)
                }
            )
        }
    }

    /**
     * rhasspy/asr/<siteId>/<sessionId>/audioCaptured (binary, Rhasspy only)
     * WAV audio data captured by ASR session
     * siteId: string - Hermes site ID (part of topic)
     * sessionId: string - current session ID (part of topic)
     * Only sent if sendAudioCaptured = true in startListening
     */
    override fun audioCaptured(sessionId: String, audioFilePath: Path) {
        scope.launch {
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
    override suspend fun recognizeIntent(sessionId: String, text: String): MqttResult {
        return publishMessage(
            MqttTopicsPublish.Query,
            createMqttMessage {
                put(MqttParams.Input, JsonPrimitive(text))
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, sessionId)
            },
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
        incomingMessages.tryEmit(
            IntentRecognitionResult(
                sessionId = jsonObject.getSessionId(),
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
        incomingMessages.tryEmit(IntentNotRecognized(jsonObject.getSessionId()))

    /**
     * hermes/handle/toggleOn
     * Enable intent handling
     */
    private fun intentHandlingToggleOn() =
        appSettingsUtil.intentHandlingToggle(false, Source.Mqtt)

    /**
     * hermes/handle/toggleOff
     * Disable intent handling
     */
    private fun intentHandlingToggleOff() =
        appSettingsUtil.intentHandlingToggle(false, Source.Mqtt)

    /**
     * hermes/tts/say (JSON)
     * Generate spoken audio for a sentence using the configured text to speech system
     * Automatically sends playBytes
     *
     * text: string - sentence to speak (required)
     *
     * volume: float? = null - volume level to speak with (0 = off, 1 = full volume)
     * id: string? = null
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     *
     * Response(s)
     * hermes/tts/sayFinished (JSON)
     */
    override suspend fun say(sessionId: String, text: String, volume: Float?, siteId: String, id: String): MqttResult {
        return publishMessage(
            MqttTopicsPublish.Say,
            createMqttMessage {
                put(MqttParams.Id, id)
                put(MqttParams.SiteId, siteId)
                put(MqttParams.Volume, volume.toString()) //TODO null volume?
                put(MqttParams.SessionId, sessionId)
                put(MqttParams.Text, JsonPrimitive(text))
            },
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
            incomingMessages.tryEmit(
                Say(
                    sessionId = jsonObject.getSessionId(),
                    text = it,
                    volume = jsonObject[MqttParams.Volume.value]?.jsonPrimitive?.floatOrNull,
                    siteId = jsonObject.getSiteId() ?: "default"
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
    private fun playBytes(byteArray: ByteArray, requestId: String) =
        incomingMessages.tryEmit(PlayBytes(requestId, byteArray))

    /**
     * hermes/audioServer/<siteId>/playFinished
     * Indicates that audio has finished playing
     * Response to hermes/audioServer/<siteId>/playBytes/<requestId>
     * siteId: string - Hermes site ID (part of topic)
     * id: string = "" - requestId from request message
     */
    private fun playFinishedCall(requestId: String) =
        incomingMessages.tryEmit(PlayFinished(id = requestId))

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
    override suspend fun playAudioRemote(audioSource: AudioSource, siteId: String, id: String): MqttResult {
        return publishMessage(
            MqttTopicsPublish.AudioOutputPlayBytes.topic
                .set(MqttTopicPlaceholder.SiteId, siteId)
                .set(MqttTopicPlaceholder.RequestId, id),
            MqttMessage(
                @Suppress("DEPRECATION")
                when (audioSource) {
                    is AudioSource.Data     -> audioSource.data
                    is AudioSource.File     -> audioSource.path.commonSource().buffer().readByteArray()
                    is AudioSource.Resource -> audioSource.fileResource.commonData(nativeApplication)
                }
            ),
        )
    }

    /**
     * hermes/audioServer/toggleOn (JSON)
     * Enable audio output
     * siteId: string = "default" - Hermes site ID
     */
    private fun audioOutputToggleOn() =
        appSettingsUtil.audioOutputToggle(false, Source.Mqtt)

    /**
     * hermes/audioServer/toggleOff (JSON)
     * Disable audio output
     * siteId: string = "default" - Hermes site ID
     */
    private fun audioOutputToggleOff() =
        appSettingsUtil.audioOutputToggle(false, Source.Mqtt)

    /**
     * hermes/audioServer/<siteId>/playFinished
     * Indicates that audio has finished playing
     * Response to hermes/audioServer/<siteId>/playBytes/<requestId>
     * siteId: string - Hermes site ID (part of topic)
     */
    override fun playFinished() {
        scope.launch {
            publishMessage(
                MqttTopicsPublish.AudioOutputPlayFinished.topic
                    .set(MqttTopicPlaceholder.SiteId, params.siteId),
                MqttMessage(ByteArray(0))
            )
        }
    }


    /**
     * rhasspy/audioServer/setVolume (JSON, Rhasspy only)
     * Set the volume at one or more sitesu
     * volume: float - volume level to set (0 = off, 1 = full volume)
     * siteId: string = "default" - Hermes site ID
     */
    private fun setVolume(jsonObject: JsonObject) =
        jsonObject[MqttParams.Volume.value]?.jsonPrimitive?.floatOrNull?.let { volume ->
            appSettingsUtil.setAudioVolume(volume, Source.Mqtt)
        }

    /**
     * check if site id is this id
     */
    private fun JsonObject.isThisSiteId(): Boolean =
        this.getSiteId() == params.siteId

    private fun JsonObject.getSessionId(): String? =
        this[MqttParams.SessionId.value]?.jsonPrimitive?.content

    private fun JsonObject.getSiteId(): String? =
        this[MqttParams.SiteId.value]?.jsonPrimitive?.content

    private fun JsonObject.getId(): String? =
        this[MqttParams.Id.value]?.jsonPrimitive?.content

    private fun JsonObject.getRequestId(): String? =
        this[MqttParams.RequestId.value]?.jsonPrimitive?.content

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
            MqttTopicsSubscription.HotWordDetected.topic
                .matches(topic) -> MqttTopicsSubscription.HotWordDetected

            MqttTopicsSubscription.IntentRecognitionResult.topic
                .matches(topic) -> MqttTopicsSubscription.IntentRecognitionResult

            MqttTopicsSubscription.PlayBytes.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId)
                .matches(topic) -> MqttTopicsSubscription.PlayBytes

            MqttTopicsSubscription.PlayFinished.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId)
                .matches(topic) -> MqttTopicsSubscription.PlayFinished

            else                -> MqttTopicsSubscription.fromTopic(topic)
        }
    }

}