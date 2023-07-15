package org.rhasspy.mobile.logic.services.webserver

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.BaseApplicationEngine
import io.ktor.server.engine.applicationEngineEnvironment
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.AudioVolumeChange
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.HotWordToggle
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source.HttpApi
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.speechtotext.StreamContent
import org.rhasspy.mobile.logic.services.webserver.WebServerResult.*
import org.rhasspy.mobile.logic.services.webserver.WebServerServiceErrorType.WakeOptionInvalid
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.ktor.getEngine
import org.rhasspy.mobile.platformspecific.ktor.installCallLogging
import org.rhasspy.mobile.platformspecific.ktor.installCompression
import org.rhasspy.mobile.platformspecific.ktor.installConnector
import org.rhasspy.mobile.platformspecific.readOnly

interface IWebServerService : IService {

    override val serviceState: StateFlow<ServiceState>

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
internal class WebServerService(
    paramsCreator: WebServerServiceParamsCreator
) : IWebServerService {

    override val logger = LogType.WebServerService.logger()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    override val serviceState = _serviceState.readOnly

    private val nativeApplication by inject<NativeApplication>()
    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val paramsFlow: StateFlow<WebServerServiceParams> = paramsCreator()
    private val params: WebServerServiceParams get() = paramsFlow.value

    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        val audioContentType = ContentType("audio", "wav")
    }

    private var server: BaseApplicationEngine? = null

    /**
     * starts server when enabled
     * logs start event
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
        if (params.isHttpServerEnabled) {
            logger.d { "initialization" }
            _serviceState.value = ServiceState.Loading

            try {
                server = buildServer()
                server?.start()
                _serviceState.value = ServiceState.Success
            } catch (exception: Exception) {
                //start error
                logger.a(exception) { "initialization error" }
                _serviceState.value = ServiceState.Exception(exception)
            }
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
    private fun buildServer(): BaseApplicationEngine {
        // TrafficStats.setTrafficStatsTag()
        logger.d { "buildServer" }
        val environment = applicationEngineEnvironment {
            installConnector(
                nativeApplication = nativeApplication,
                port = params.httpServerPort,
                isUseSSL = params.isHttpServerSSLEnabled,
                keyStoreFile = "${FolderType.CertificateFolder.WebServer}/${params.httpServerSSLKeyStoreFile ?: ""}",
                keyStorePassword = params.httpServerSSLKeyStorePassword,
                keyAlias = params.httpServerSSLKeyAlias,
                keyPassword = params.httpServerSSLKeyPassword
            )
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

        return getEngine(environment = environment)
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
            WebServerPath.values().forEach { path ->
                when (path.type) {
                    WebServerPath.WebServerCallType.POST -> post(path.path) {
                        evaluateCall(path, call)
                    }

                    WebServerPath.WebServerCallType.GET -> get(path.path) {
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
        try {
            val result = when (path) {
                WebServerPath.ListenForCommand -> listenForCommand()
                WebServerPath.ListenForWake -> listenForWake(call)
                WebServerPath.PlayRecordingPost -> playRecordingPost()
                WebServerPath.PlayRecordingGet -> playRecordingGet(call)
                WebServerPath.PlayWav -> playWav(call)
                WebServerPath.SetVolume -> setVolume(call)
                WebServerPath.StartRecording -> startRecording()
                WebServerPath.StopRecording -> stopRecording()
                WebServerPath.Say -> say(call)
                WebServerPath.Mqtt -> mqtt(call)
            }

            when (result) {
                is Accepted -> {
                    call.respond(HttpStatusCode.Accepted)
                }

                is Error -> {
                    logger.d { "evaluateCall BadRequest ${result.errorType.description}" }
                    call.respond(HttpStatusCode.BadRequest, result.errorType.description)
                }

                Ok -> {
                    call.respond(HttpStatusCode.OK)
                }

                else -> {

                }
            }
            _serviceState.value = ServiceState.Success

        } catch (exception: Exception) {
            logger.e(exception) { "evaluateCall error" }
            _serviceState.value = ServiceState.Exception(exception)
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
        serviceMiddleware.action(WakeWordDetected(HttpApi, "remote"))
        return Ok
    }


    /**
     * /api/listen-for-wake
     * POST "on" to have Rhasspy listen for a wake word
     * POST "off" to disable wake word
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private suspend fun listenForWake(call: ApplicationCall): WebServerResult {
        val action = when (call.receive<String>()) {
            "on" -> true
            "off" -> false
            else -> null
        }

        return action?.let {
            serviceMiddleware.action(HotWordToggle(it))
            Accepted(it.toString())
        } ?: Error(WakeOptionInvalid)
    }


    /**
     * /api/play-recording
     * POST to play last recorded voice command
     */
    private fun playRecordingPost(): WebServerResult {
        serviceMiddleware.action(PlayStopRecording)
        return Ok
    }


    /**
     * /api/play-recording
     * GET to download WAV data from last recorded voice command
     */
    private suspend fun playRecordingGet(call: ApplicationCall): WebServerResult? {
        call.respond(StreamContent(serviceMiddleware.getRecordedFile()))
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
            Error(WebServerServiceErrorType.AudioContentTypeWarning)
        } else Ok
        //play even without content type
        serviceMiddleware.action(PlayAudio(HttpApi, call.receive()))
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
        return result.toFloatOrNull()?.let {
            if (it in 0f..1f) {
                serviceMiddleware.action(AudioVolumeChange(it))
                Accepted(it.toString())
            } else {
                logger.w { "setVolume VolumeValueOutOfRange $it" }
                Error(WebServerServiceErrorType.VolumeValueOutOfRange)
            }
        } ?: run {
            logger.w { "setVolume VolumeValueInvalid $result" }
            Error(WebServerServiceErrorType.VolumeValueInvalid)
        }
    }

    /**
     * /api/start-recording
     * POST to have Rhasspy start recording a voice command
     * actually starts a session
     */
    private fun startRecording(): WebServerResult {
        serviceMiddleware.action(StartListening(HttpApi, false))
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
        serviceMiddleware.action(StopListening(HttpApi))
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
        serviceMiddleware.action(SayText(call.receive()))
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
        serviceMiddleware.action(Mqtt(topic, call.receive()))
        return Ok
    }

}