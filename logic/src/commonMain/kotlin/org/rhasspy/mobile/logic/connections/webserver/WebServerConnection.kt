package org.rhasspy.mobile.logic.connections.webserver

import co.touchlab.kermit.Logger
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.dataconversion.DataConversion
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.contentType
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.inject
import org.koin.dsl.module
import org.rhasspy.mobile.data.connection.LocalWebserverConnectionData
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.data.service.ConnectionState.ErrorState
import org.rhasspy.mobile.logic.connections.IConnection
import org.rhasspy.mobile.logic.connections.http.StreamContent
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionErrorType.WakeOptionInvalid
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent.*
import org.rhasspy.mobile.logic.connections.webserver.WebServerResult.*
import org.rhasspy.mobile.logic.local.file.IFileStorage
import org.rhasspy.mobile.logic.local.settings.IAppSettingsUtil
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.extensions.commonExists
import org.rhasspy.mobile.platformspecific.extensions.commonInternalFilePath
import org.rhasspy.mobile.platformspecific.ktor.buildServer
import org.rhasspy.mobile.platformspecific.ktor.installCallLogging
import org.rhasspy.mobile.platformspecific.ktor.installCompression
import org.rhasspy.mobile.platformspecific.ktor.installConnector
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IWebServerConnection : IConnection {

    val incomingMessages: Flow<WebServerConnectionEvent>

}

/**
 * Web server service holds all routes for WebServerPath values
 *
 * Responds to any request
 * - no parameter and valid : OK
 * - parameter and valid: Accepted
 * - parameter Invalid: BadRequest
 * - else: determined by Ktor
 */
internal class WebServerConnection(
    private val appSettingsUtil: IAppSettingsUtil,
    private val fileStorage: IFileStorage,
    private val mqttConnection: IMqttConnection,
    private val serviceMiddleware: IServiceMiddleware
) : IWebServerConnection {

    private val logger = Logger.withTag("WebServerConnection")
    override val incomingMessages = MutableSharedFlow<WebServerConnectionEvent>()

    override val connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Pending)

    private val nativeApplication by inject<NativeApplication>()

    private var params = ConfigurationSetting.localWebserverConnection.value

    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        val audioContentType = ContentType("audio", "wav")
    }

    private var server: ApplicationEngine? = null

    /**
     * starts server when enabled
     * logs start event
     */
    init {
        scope.launch {
            ConfigurationSetting.localWebserverConnection.data.collectLatest(::collectParams)
        }
    }

    private fun collectParams(params: LocalWebserverConnectionData) {
        this.params = params
        stop()
        start()
    }

    private fun start() {
        if (params.isEnabled) {
            logger.d { "initialization" }
            connectionState.value = ConnectionState.Loading

            if (params.isSSLEnabled && !params.keyStoreFile?.let { Path.commonInternalFilePath(nativeApplication, it) }.commonExists()) {
                connectionState.value = ErrorState.Error(MR.strings.certificate_missing.stable)
                return
            }

            try {
                server = buildServer(
                    module = { createModules() },
                    configure = {
                        installConnector(
                            nativeApplication = nativeApplication,
                            port = params.port,
                            isUseSSL = params.isSSLEnabled,
                            keyStoreFile = "${FolderType.CertificateFolder.WebServer}/${params.keyStoreFile ?: ""}",
                            keyStorePassword = params.keyStorePassword,
                            keyAlias = params.keyAlias,
                            keyPassword = params.keyPassword
                        )
                    },
                )
                server?.start()
                connectionState.value = ConnectionState.Success
            } catch (exception: Exception) {
                //start error
                logger.a(exception) { "initialization error" }
                connectionState.value = ErrorState.Exception(exception)
            }
        } else {
            connectionState.value = ConnectionState.Disabled
        }
    }

    /**
     * closes server and scope
     */
    private fun stop() {
        logger.d { "onClose" }
        server?.stop()
    }

    /**
     * build server with routing and addons
     */
    private fun Application.createModules() {
        // TrafficStats.setTrafficStatsTag()
        logger.d { "buildServer" }

        module {
            //install(WebSockets)
            installCallLogging()
            install(DataConversion)
            //Greatly reduces the amount of data that's needed to be sent to the client by
            //gzipping outgoing content when applicable.
            installCompression()

            // configures Cross-Origin Resource Sharing. CORS is needed to make calls from arbitrary
            // JavaScript clients, and helps us prevent issues down the line.
            install(CORS) {
                methods.add(HttpMethod.Get)
                methods.add(HttpMethod.Post)
                methods.add(HttpMethod.Delete)
                anyHost()
            }

            buildStatusPages()

            buildRouting()
        }
    }

    /**
     * evaluates HttpStatusCode and updates event state
     */
    private fun Application.buildStatusPages() {
        install(StatusPages) {
            HttpStatusCode.allStatusCodes
                .forEach {
                    status(it) { call, status ->
                        if (status != HttpStatusCode.OK && status != HttpStatusCode.Accepted) {
                            call.respondText(text = status.description, status = status)
                        }
                    }
                }
        }
    }

    /**
     * creates Routing for all WebServerPath values
     */
    private fun Application.buildRouting() {
        routing {
            WebServerPath.entries.forEach { path ->
                when (path.type) {
                    WebServerPath.WebServerCallType.POST -> post(path.path) {
                        evaluateCall(path, call)
                    }

                    WebServerPath.WebServerCallType.GET  -> get(path.path) {
                        evaluateCall(path, call)
                    }
                }
            }
        }
    }

    /**
     * evaluates any call
     */
    private suspend fun evaluateCall(path: WebServerPath, call: ApplicationCall) {
        logger.d { "evaluateCall ${path.path} ${call.parameters}" }
        connectionState.value = try {
            val result = when (path) {
                WebServerPath.ListenForCommand  -> listenForCommand()
                WebServerPath.ListenForWake     -> listenForWake(call)
                WebServerPath.PlayRecordingPost -> playRecordingPost()
                WebServerPath.PlayRecordingGet  -> playRecordingGet(call)
                WebServerPath.PlayWav           -> playWav(call)
                WebServerPath.SetVolume         -> setVolume(call)
                WebServerPath.StartRecording    -> startRecording()
                WebServerPath.StopRecording     -> stopRecording()
                WebServerPath.Say               -> say(call)
                WebServerPath.Mqtt              -> mqtt(call)
            }

            when (result) {
                is Accepted -> call.respond(HttpStatusCode.Accepted, result.data)

                is Error    -> {
                    logger.d { "evaluateCall BadRequest ${result.errorType.description}" }
                    call.respond(HttpStatusCode.BadRequest, result.errorType.description)
                }

                Ok          -> call.respond(HttpStatusCode.OK)
                else        -> Unit
            }
            ConnectionState.Success

        } catch (exception: Exception) {
            logger.e(exception) { "evaluateCall error" }
            ErrorState.Exception(exception)
        }
    }

    /**
     * /api/listen-for-command
     * POST to wake Rhasspy up and start listening for a voice command
     * Returns intent JSON when command is finished
     * ?nohass=true - stop Rhasspy from handling the intent
     * ?timeout=<seconds> - override default command timeout
     * ?entity=<entity>&value=<value> - set custom entities/values in recognized intent
     */
    private fun listenForCommand(): WebServerResult {
        incomingMessages.tryEmit(WebServerListenForCommand)
        return Ok
    }


    /**
     * /api/listen-for-wake
     * POST "on" to have Rhasspy listen for a wake word
     * POST "off" to disable wake word
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private suspend fun listenForWake(call: ApplicationCall): WebServerResult {
        val value = call.receive<String>()
        val action = when (value) {
            "on"  -> true
            "off" -> false
            else  -> null
        }

        return action?.let {
            appSettingsUtil.hotWordToggle(it, Source.HttpApi)
            Accepted(value)
        } ?: Error(WakeOptionInvalid)
    }


    /**
     * /api/play-recording
     * POST to play last recorded voice command
     */
    private fun playRecordingPost(): WebServerResult {
        serviceMiddleware.playRecording() //TODO to output on output
        return Ok
    }


    /**
     * /api/play-recording
     * GET to download WAV data from last recorded voice command
     */
    private suspend fun playRecordingGet(call: ApplicationCall): WebServerResult? {
        call.respond(StreamContent(fileStorage.speechToTextAudioFile))
        return null
    }

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private suspend fun playWav(call: ApplicationCall): WebServerResult {
        val result = if (call.request.contentType() != audioContentType) {
            logger.w { "playWav wrong content type ${call.request.contentType()}" }
            Error(WebServerConnectionErrorType.AudioContentTypeWarning)
        } else Ok
        //play even without content type
        incomingMessages.tryEmit(WebServerPlayWav(call.receive()))

        return result
    }

    /**
     * /api/set-volume
     * POST to set volume at one or more sites
     * Body text is volume level (0 = off, 1 = full volume)
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private suspend fun setVolume(call: ApplicationCall): WebServerResult {
        //double and float or double not working but string??
        val result = call.receive<String>()
        return result.toFloatOrNull()?.let { volume ->
            if (volume in 0f..1f) {
                appSettingsUtil.setAudioVolume(volume, Source.Mqtt)
                Accepted(volume.toString())
            } else {
                logger.w { "setVolume VolumeValueOutOfRange $volume" }
                Error(WebServerConnectionErrorType.VolumeValueOutOfRange)
            }
        } ?: run {
            logger.w { "setVolume VolumeValueInvalid $result" }
            Error(WebServerConnectionErrorType.VolumeValueInvalid)
        }
    }

    /**
     * /api/start-recording
     * POST to have Rhasspy start recording a voice command
     * actually starts a session
     */
    private fun startRecording(): WebServerResult {
        incomingMessages.tryEmit(WebServerStartRecording)
        return Ok
    }

    /**
     * /api/stop-recording
     * POST to have Rhasspy stop recording and process recorded data as a voice command
     * handled just like silence was detected
     * (if dialogue management was set to local)
     *
     * Not Yet:
     * Returns intent JSON when command has been processed
     * ?nohass=true - stop Rhasspy from handling the intent
     * ?entity=<entity>&value=<value> - set custom entity/value in recognized intent
     */
    private fun stopRecording(): WebServerResult {
        incomingMessages.tryEmit(WebServerStopRecording)
        return Ok
    }

    /**
     * /api/say
     *
     * custom endpoint for the rhasspy app
     * POST text to have Rhasspy use the text-to-speech endpoint to translate the text to audio
     * Afterwards Rhasspy will use the audio endpoint to play the audio
     * just like using say in the ui start screen but remote
     */
    private suspend fun say(call: ApplicationCall): WebServerResult {
        incomingMessages.tryEmit(WebServerSay(call.receive()))
        return Ok
    }

    /**
     * /api/mqtt
     *
     * POST JSON payload to /api/mqtt/<topic>
     * the mqtt Message will be evaluated but not send to mqtt broker
     */
    private suspend fun mqtt(call: ApplicationCall): WebServerResult {
        val topic = call.request.path().substringAfter("/mqtt")
        mqttConnection.onMessageReceived(topic, call.receive())
        return Ok
    }

}