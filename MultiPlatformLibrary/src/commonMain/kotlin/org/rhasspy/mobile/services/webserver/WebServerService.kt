package org.rhasspy.mobile.services.webserver

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.dataconversion.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.middleware.Action
import org.rhasspy.mobile.middleware.Action.AppSettingsAction
import org.rhasspy.mobile.middleware.Action.DialogAction
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.nativeutils.getEngine
import org.rhasspy.mobile.nativeutils.installCallLogging
import org.rhasspy.mobile.nativeutils.installCompression
import org.rhasspy.mobile.nativeutils.installConnector
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService

/**
 * Web server service holds all routes for WebServerPath values
 *
 * Responds to any request
 * - no parameter and valid : OK
 * - parameter and valid: Accepted
 * - parameter Invalid: BadRequest
 * - else: determined by Ktor
 */
class WebServerService : IService() {
    private val logger = LogType.WebServerService.logger()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    val serviceState = _serviceState.readOnly

    private val params by inject<WebServerServiceParams>()

    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val scope = CoroutineScope(Dispatchers.Default)
    private val audioContentType = ContentType("audio", "wav")

    private lateinit var server: BaseApplicationEngine

    /**
     * starts server when enabled
     * logs start event
     */
    init {
        logger.d { "initialization" }
        if (params.isHttpServerEnabled) {
            try {
                server = buildServer()
                server.start()
            } catch (exception: Exception) {
                //start error
                logger.e(exception) { "initialization error" }
            }
        }
    }

    /**
     * closes server and scope
     */
    override fun onClose() {
        logger.d { "onClose" }
        if (::server.isInitialized) {
            server.stop()
        }
        scope.cancel()
    }

    /**
     * build server with routing and addons
     */
    private fun buildServer(): BaseApplicationEngine {
        logger.d { "buildServer" }
        val environment = applicationEngineEnvironment {
            installConnector(
                port = params.httpServerPort,
                isUseSSL = params.isHttpServerSSLEnabled,
                keyStoreFile = "certificates/${params.httpServerSSLKeyStoreFile ?: ""}",
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
        logger.d { "evaluateCall ${path.path} ${call.request}" }
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
            }

            when (result) {
                is WebServerResult.Accepted -> {
                    call.respond(HttpStatusCode.Accepted)
                }
                is WebServerResult.Error -> {
                    logger.d { "evaluateCall BadRequest ${result.errorType.description}" }
                    call.respond(HttpStatusCode.BadRequest, result.errorType.description)
                }
                WebServerResult.Ok -> {
                    call.respond(HttpStatusCode.OK)
                }
                else -> {

                }
            }

        } catch (exception: Exception) {
            logger.e(exception) { "evaluateCall error" }
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
        serviceMiddleware.action(DialogAction.WakeWordDetected(Source.HttpApi, "remote"))
        return WebServerResult.Ok
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

        action?.let {
            serviceMiddleware.action(AppSettingsAction.HotWordToggle(it))
            return WebServerResult.Accepted(it.toString())
        } ?: run {
            return WebServerResult.Error(WebServerServiceErrorType.WakeOptionInvalid)
        }
    }


    /**
     * /api/play-recording
     * POST to play last recorded voice command
     */
    private fun playRecordingPost(): WebServerResult {
        serviceMiddleware.action(Action.PlayRecording)
        return WebServerResult.Ok
    }


    /**
     * /api/play-recording
     * GET to download WAV data from last recorded voice command
     */
    private suspend fun playRecordingGet(call: ApplicationCall): WebServerResult? {
        call.respondBytes(
            bytes = serviceMiddleware.getRecordedData(),
            contentType = audioContentType
        )
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
            WebServerResult.Error(WebServerServiceErrorType.AudioContentTypeWarning)
        } else WebServerResult.Ok
        //play even without header
        serviceMiddleware.action(DialogAction.PlayAudio(Source.HttpApi, call.receive()))
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
                serviceMiddleware.action(AppSettingsAction.AudioVolumeChange(it))
                return WebServerResult.Accepted(it.toString())
            } else {
                logger.w { "setVolume VolumeValueOutOfRange $it" }
                return WebServerResult.Error(WebServerServiceErrorType.VolumeValueOutOfRange)
            }
        } ?: run {
            logger.w { "setVolume VolumeValueInvalid $result" }
            return WebServerResult.Error(WebServerServiceErrorType.VolumeValueInvalid)
        }
    }

    /**
     * /api/start-recording
     * POST to have Rhasspy start recording a voice command
     * actually starts a session
     */
    private fun startRecording(): WebServerResult {
        serviceMiddleware.action(DialogAction.StartListening(Source.HttpApi, false))
        return WebServerResult.Ok
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
        serviceMiddleware.action(DialogAction.StopListening(Source.HttpApi))
        return WebServerResult.Ok
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
        serviceMiddleware.action(DialogAction.PlayAudio(Source.HttpApi, call.receive()))
        return WebServerResult.Ok
    }

}