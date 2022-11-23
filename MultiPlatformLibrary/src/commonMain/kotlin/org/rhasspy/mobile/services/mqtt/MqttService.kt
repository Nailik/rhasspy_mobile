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
import org.koin.core.qualifier.named
import org.rhasspy.mobile.logger.Event
import org.rhasspy.mobile.logger.EventLogger
import org.rhasspy.mobile.logger.EventTag
import org.rhasspy.mobile.logger.EventType
import org.rhasspy.mobile.mqtt.*
import org.rhasspy.mobile.nativeutils.MqttClient
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.ServiceResponse
import org.rhasspy.mobile.services.dialogManager.IDialogManagerService
import org.rhasspy.mobile.services.settings.AppSettingsService
import org.rhasspy.mobile.services.statemachine.StateMachineService

class MqttService : IService() {

    enum class ErrorType {
        NotInitialized,
        InvalidVolume,
        InvalidTopic,
        DifferentSiteId,
        SubscriptionError,
        ConnectionError,
        AlreadyConnected
    }

    private val params by inject<MqttServiceParams>()

    private val stateMachineService by inject<StateMachineService>()
    private val dialogManagerService by inject<IDialogManagerService>()
    private val appSettingsService by inject<AppSettingsService>()

    private val logger = Logger.withTag("MqttService")
    private val eventLogger by inject<EventLogger>(named(EventTag.MqttService.name))

    private var scope = CoroutineScope(Dispatchers.Default)
    private val url = "tcp://${params.mqttHost}:${params.mqttPort}"

    private var client: MqttClient? = null
    private var retryJob: Job? = null
    private val id = uuid4().variant

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.readOnly


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
            val startEvent = eventLogger.event(EventType.MqttClientStart)

            try {
                //starting
                startEvent.loading()

                client = buildClient()
                scope.launch {
                    if (connectClient()) {
                        startEvent.success()
                        subscribeTopics()
                    } else {
                        startEvent.error(ErrorType.ConnectionError.toString())
                    }
                }

            } catch (e: Exception) {
                //start error
                startEvent.error(e)
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
        client?.disconnect()
        _isConnected.value = false
        retryJob?.cancel()
        retryJob = null
        scope.cancel()
    }

    /**
     * connects client to server and returns if client is now connected
     */
    private suspend fun connectClient(): Boolean {
        val connectEvent = eventLogger.event(EventType.MqttClientConnecting)

        if (client?.isConnected == false) {
            //only if not connected
            connectEvent.loading()
            //connect to server
            client?.connect(params.mqttServiceConnectionOptions)?.also {
                connectEvent.error("${it.statusCode.name} ${it.msg}")
            } ?: run{
                connectEvent.success()
            }
        } else {
            connectEvent.success(ErrorType.AlreadyConnected.toString())
        }

        //update value, may be used from reconnect
        _isConnected.value = client?.isConnected == true
        return _isConnected.value
    }


    /**
     * try to reconnect after disconnect
     */
    private fun onDisconnect(error: Throwable) {
        val disconnectEvent = eventLogger.event(EventType.MqttClientDisconnected)
        disconnectEvent.error(error)

        _isConnected.value = client?.isConnected == true

        if (retryJob?.isActive != true) {
            retryJob = scope.launch {

                var reconnectEvent: Event? = null

                client?.also {
                    while (!it.isConnected) {
                        reconnectEvent = eventLogger.event(EventType.MqttClientReconnect)
                        reconnectEvent?.loading()
                        connectClient()
                        delay(params.retryInterval)
                        reconnectEvent?.error(ErrorType.ConnectionError.toString())
                    }
                    reconnectEvent?.success()
                    retryJob?.cancel()
                    retryJob = null
                }
            }
        }
    }


    private fun onMessageReceived(topic: String, message: MqttMessage) {
        logger.v { "onMessageReceived id ${message.msgId} $topic" }

        if (message.msgId == id) {
            //ignore all messages that i have send
            logger.v { "message ignored, was same id as send by myself" }
            return
        }

        val receiveEvent = eventLogger.event(EventType.MqttClientReceived)

        MqttTopicsSubscription.fromTopic(topic)?.also { mqttTopic ->

            try {

                if (!mqttTopic.topic.contains(MqttTopicPlaceholder.SiteId.toString())) {
                    //site id in payload
                    //decode json object
                    val jsonObject = Json.decodeFromString<JsonObject>(message.payload.decodeToString())
                    //validate site id
                    if (jsonObject.isThisSiteId()) {
                        when (mqttTopic) {
                            MqttTopicsSubscription.StartSession -> startSession()
                            MqttTopicsSubscription.EndSession -> endSession(jsonObject)
                            MqttTopicsSubscription.SessionStarted -> sessionStarted(jsonObject)
                            MqttTopicsSubscription.SessionEnded -> sessionEnded(jsonObject)
                            MqttTopicsSubscription.HotWordToggleOn -> hotWordToggleOn()
                            MqttTopicsSubscription.HotWordToggleOff -> hotWordToggleOff()
                            MqttTopicsSubscription.AsrStartListening -> startListening(jsonObject)
                            MqttTopicsSubscription.AsrStopListening -> stopListening(jsonObject)
                            MqttTopicsSubscription.AsrTextCaptured -> asrTextCaptured(jsonObject)
                            MqttTopicsSubscription.AsrError -> asrError(jsonObject)
                            MqttTopicsSubscription.IntentNotRecognized -> intentNotRecognized(jsonObject)
                            MqttTopicsSubscription.IntentHandlingToggleOn -> intentHandlingToggleOn()
                            MqttTopicsSubscription.IntentHandlingToggleOff -> intentHandlingToggleOff()
                            MqttTopicsSubscription.AudioOutputToggleOff -> audioOutputToggleOff()
                            MqttTopicsSubscription.AudioOutputToggleOn -> audioOutputToggleOn()
                            MqttTopicsSubscription.SetVolume -> if (!setVolume(jsonObject)) {
                                receiveEvent.error(ErrorType.InvalidVolume.toString())
                            }
                            else -> {
                                when {
                                    MqttTopicsSubscription.HotWordDetected.topic.matches(topic) -> hotWordDetectedCalled(topic)
                                    MqttTopicsSubscription.IntentRecognitionResult.topic.matches(topic) -> intentRecognitionResult(jsonObject)
                                    else -> receiveEvent.error(ErrorType.InvalidTopic.toString())
                                }
                            }
                        }
                    } else {
                        receiveEvent.warning(ErrorType.DifferentSiteId.toString())
                    }

                } else {
                    //site id in topic
                    when {
                        MqttTopicsSubscription.PlayBytes.topic
                            .set(MqttTopicPlaceholder.SiteId, params.siteId)
                            .matches(topic) -> playBytes(message.payload)
                        else -> receiveEvent.error(ErrorType.InvalidTopic.toString())
                    }
                }

            } catch (e: Exception) {
                receiveEvent.error(e)
            }

        } ?: run {
            receiveEvent.error(ErrorType.InvalidTopic.toString())
        }
    }

    /**
     * Subscribes to topics that are necessary
     */
    private suspend fun subscribeTopics() {
        val subscribeTopics = eventLogger.event(EventType.MqttClientSubscribing)
        subscribeTopics.loading()
        var hasError = false
        var hasSuccess = false

        //subscribe to topics with this site id (if contained in topic, currently only in PlayBytes)
        MqttTopicsSubscription.values().forEach { mqttTopic ->
            val subscribeEvent = eventLogger.event(EventType.MqttClientSubscribing)
            subscribeEvent.loading()

            try {
                client?.subscribe(mqttTopic.topic.set(MqttTopicPlaceholder.SiteId, params.siteId))?.also {
                    hasError = true
                    subscribeEvent.error("$mqttTopic ${it.statusCode.name} ${it.msg}")
                } ?: run {
                    hasSuccess = true
                    subscribeEvent.success("$mqttTopic")
                }
            } catch (e: Exception) {
                subscribeEvent.error(e)
            }
        }

        if (hasError) {
            if (hasSuccess) {
                //some worked
                subscribeTopics.warning()
            } else {
                //none worked
                subscribeTopics.error(ErrorType.SubscriptionError.toString())
            }
        } else {
            subscribeTopics.success()
        }
    }


    /**
     * published new messages
     */
    private suspend fun publishMessage(topic: String, message: MqttMessage): ServiceResponse<*> {
        val publishEvent = eventLogger.event(EventType.MqttClientPublish)
        message.msgId = id

        return client?.let { mqttClient ->
            publishEvent.loading()
            mqttClient.publish(topic, message)?.let {
                publishEvent.error("$topic ${it.statusCode.name} ${it.msg}")
                ServiceResponse.Error(Exception(it.statusCode.name))
            } ?: run {
                publishEvent.success(topic)
                ServiceResponse.Success(Unit)
            }
        } ?: run {
            publishEvent.error(ErrorType.NotInitialized.toString())
            ServiceResponse.NotInitialized
        }
    }

    private suspend fun publishMessage(mqttTopic: MqttTopicsPublish, message: MqttMessage): ServiceResponse<*> =
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
    private fun startSession() =
        dialogManagerService.startSessionMqtt()

    /**
     * https://rhasspy.readthedocs.io/en/latest/reference/#dialoguemanager_endsession
     *
     * hermes/dialogueManager/endSession (JSON)
     * Requests that a session be terminated nominally
     * sessionId: string - current session ID (required)
     */
    private fun endSession(jsonObject: JsonObject) =
        dialogManagerService.endSessionMqtt(
            jsonObject.getSessionId()
        )

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
        dialogManagerService.startedSessionMqtt(
            jsonObject.getSessionId()
        )

    /**
     * hermes/dialogueManager/sessionStarted (JSON)
     * Indicates a session has started
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     *
     * Response to [hermes/dialogueManager/startSession]
     * Also used when session has started for other reasons
     */
    suspend fun sessionStarted(): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.SessionStarted,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, dialogManagerService.sessionId)
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
        dialogManagerService.sessionEndedMqtt(
            jsonObject.getSessionId()
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
    suspend fun sessionEnded(): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.SessionEnded,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, dialogManagerService.sessionId)
            }
        )

    /**
     * hermes/dialogueManager/intentNotRecognized (JSON)
     *
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     */
    suspend fun intentNotRecognized(): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.IntentNotRecognizedInSession,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, dialogManagerService.sessionId)
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
        appSettingsService.hotWordToggleOnMqtt()

    /**
     * hermes/hotword/toggleOff (JSON)
     * Disables hotword detection
     * siteId: string = "default" - Hermes site ID
     * reason: string = "" - Reason for toggle off
     */
    private fun hotWordToggleOff() =
        appSettingsService.hotWordToggleOffMqtt()


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
                    dialogManagerService.hotWordDetectedMqtt(it[2])
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
    private fun startListening(jsonObject: JsonObject) {
        dialogManagerService.startListeningMqtt(
            jsonObject.getSessionId(),
            jsonObject[MqttParams.SendAudioCaptured.value]?.jsonPrimitive?.booleanOrNull == true
        )
    }

    /**
     * hermes/asr/startListening (JSON)
     * Tell ASR system to start recording/transcribing
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     *
     * stopOnSilence: bool = true - detect silence and automatically end voice command (Rhasspy only)
     */
    suspend fun startListening(): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.AsrStartListening,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, dialogManagerService.sessionId)
                put(MqttParams.StopOnSilence, true)
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
        dialogManagerService.stopListeningMqtt(
            jsonObject.getSessionId()
        )

    /**
     * hermes/asr/stopListening (JSON)
     * Tell ASR system to stop recording
     * Emits textCaptured if silence has was not detected earlier
     * siteId: string = "default" - Hermes site ID
     * sessionId: string = "" - current session ID
     */
    suspend fun stopListening(): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.AsrStopListening,
            createMqttMessage {
                put(MqttParams.SessionId, dialogManagerService.sessionId)
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
        dialogManagerService.intentTranscribedMqtt(
            jsonObject.getSessionId(),
            jsonObject[MqttParams.Text.value]?.jsonPrimitive?.content
        )

    /**
     * hermes/asr/textCaptured (JSON)
     * Successful transcription, sent either when silence is detected or on stopListening
     *
     * text: string - transcription text
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    suspend fun asrTextCaptured(text: String?): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.AsrTextCaptured,
            createMqttMessage {
                put(MqttParams.Text, JsonPrimitive(text))
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, dialogManagerService.sessionId)
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
        dialogManagerService.intentTranscribedErrorMqtt(
            jsonObject.getSessionId()
        )


    /**
     * hermes/error/asr (JSON)
     * Sent when an error occurs in the ASR system
     *
     * siteId: string = "default" - Hermes site ID
     * sessionId: string? = null - current session ID
     */
    suspend fun asrError(): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.AsrError,
            createMqttMessage {
                put(MqttParams.SessionId, dialogManagerService.sessionId)
            }
        )

    /**
     * rhasspy/asr/<siteId>/<sessionId>/audioCaptured (binary, Rhasspy only)
     * WAV audio data captured by ASR session
     * siteId: string - Hermes site ID (part of topic)
     * sessionId: string - current session ID (part of topic)
     * Only sent if sendAudioCaptured = true in startListening
     */
    suspend fun audioCaptured(byteData: List<Byte>) {
        publishMessage(
            MqttTopicsPublish.AudioCaptured.topic
                .set(MqttTopicPlaceholder.SiteId, params.siteId)
                .set(MqttTopicPlaceholder.SessionId, dialogManagerService.sessionId),
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
    suspend fun recognizeIntent(text: String): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.Query,
            createMqttMessage {
                put(MqttParams.Input, JsonPrimitive(text))
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, dialogManagerService.sessionId)
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
        dialogManagerService.intentRecognizedMqtt(
            sessionId = jsonObject.getSessionId(),
            intentName = jsonObject[MqttParams.Intent.value]?.jsonObject?.get(MqttParams.IntentName.value)?.jsonPrimitive?.content,
            intent = jsonObject.toString()
        )

    /**
     * hermes/dialogueManager/intentNotRecognized (JSON)
     *
     * sessionId: string - current session ID
     * siteId: string = "default" - Hermes site ID
     */
    private fun intentNotRecognized(jsonObject: JsonObject) =
        dialogManagerService.intentNotRecognizedMqtt(jsonObject.getSessionId())

    /**
     * hermes/handle/toggleOn
     * Enable intent handling
     */
    private fun intentHandlingToggleOn() =
        appSettingsService.intentHandlingToggleOnMqtt()

    /**
     * hermes/handle/toggleOff
     * Disable intent handling
     */
    private fun intentHandlingToggleOff() =
        appSettingsService.intentHandlingToggleOffMqtt()

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
    suspend fun say(text: String): ServiceResponse<*> =
        publishMessage(
            MqttTopicsPublish.Say,
            createMqttMessage {
                put(MqttParams.SiteId, params.siteId)
                put(MqttParams.SessionId, dialogManagerService.sessionId)
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
        stateMachineService.playAudioMqtt(payload.toList())

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
        appSettingsService.audioOutputToggleOffMqtt()

    /**
     * hermes/audioServer/toggleOn (JSON)
     * Enable audio output
     * siteId: string = "default" - Hermes site ID
     */
    private fun audioOutputToggleOn() =
        appSettingsService.audioOutputToggleOnMqtt()

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
            appSettingsService.setAudioVolumeMqtt(it)
            true
        } ?: false


    /**
     * check if site id is this id
     */
    private fun JsonObject.isThisSiteId(): Boolean =
        this[MqttParams.SiteId.value]?.jsonPrimitive?.content == params.siteId

    private fun JsonObject.getSessionId(): String? =
        this[MqttParams.SessionId.value]?.jsonPrimitive?.content

    private fun JsonObjectBuilder.put(key: MqttParams, element: Boolean): JsonElement? = put(key.value, element)
    private fun JsonObjectBuilder.put(key: MqttParams, element: JsonElement): JsonElement? = put(key.value, element)
    private fun JsonObjectBuilder.put(key: MqttParams, value: String?): JsonElement? = put(key.value, value)
    private fun String.set(key: MqttTopicPlaceholder, value: String): String = this.replace(key.placeholder, value)
    private fun String.matches(regex: String): Boolean {
        return this
            .replace("/", "\\/") //escape slashes
            .replace("+", ".*") //replace wildcard with regex text
            .toRegex()
            .matches(regex)
    }

}