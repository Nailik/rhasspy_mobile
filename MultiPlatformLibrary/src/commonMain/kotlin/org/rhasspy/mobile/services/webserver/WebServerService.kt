package org.rhasspy.mobile.services.webserver

import co.touchlab.kermit.Logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
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
import org.koin.core.component.inject
import org.rhasspy.mobile.middleware.ErrorType.WebServerServiceErrorType
import org.rhasspy.mobile.middleware.ErrorType.WebServerServiceErrorType.*
import org.rhasspy.mobile.middleware.Event
import org.rhasspy.mobile.middleware.EventType.WebServerServiceEventType.Received
import org.rhasspy.mobile.middleware.EventType.WebServerServiceEventType.Start
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.middleware.action.WebServerAction.*
import org.rhasspy.mobile.middleware.action.WebServerRequest.PlayRecordingGet
import org.rhasspy.mobile.nativeutils.installCallLogging
import org.rhasspy.mobile.nativeutils.installCompression
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

    private val params by inject<WebServerServiceParams>()

    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val logger = Logger.withTag("WebServerService")

    private val scope = CoroutineScope(Dispatchers.Default)
    private val audioContentType = ContentType("audio", "wav")

    private lateinit var server: CIOApplicationEngine

    /**
     * starts server when enabled
     * logs start event
     */
    init {
        if (params.isHttpServerEnabled) {
            val startEvent = serviceMiddleware.createEvent(Start)

            try {
                server = buildServer(params.httpServerPort)
                server.start()
                //successfully start
                startEvent.success()
            } catch (e: Exception) {
                //start error
                startEvent.error(e)
            }
        } else {
            //server disabled
            logger.v { "Server disabled" }
        }
    }

    /**
     * closes server and scope
     */
    override fun onClose() {
        if (::server.isInitialized) {
            server.stop()
        }
        scope.cancel()
    }

    /**
     * build server with routing and addons
     */
    private fun buildServer(port: Int): CIOApplicationEngine {
        return embeddedServer(factory = CIO, port = port, watchPaths = emptyList()) {
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
        val callEvent = serviceMiddleware.createEvent(Received, path.path)
        try {
            val result = when (path) {
                WebServerPath.ListenForCommand -> listenForCommand()
                WebServerPath.ListenForWake -> listenForWake(call)
                WebServerPath.PlayRecordingPost -> playRecordingPost()
                WebServerPath.PlayRecordingGet -> playRecordingGet(call)
                WebServerPath.PlayWav -> playWav(call, callEvent)
                WebServerPath.SetVolume -> setVolume(call)
                WebServerPath.StartRecording -> startRecording()
                WebServerPath.StopRecording -> stopRecording()
                WebServerPath.Say -> say(call)
            }

            when (result) {
                is WebServerResult.Accepted -> {
                    call.respond(HttpStatusCode.Accepted)
                    callEvent.success(result.data)
                }
                is WebServerResult.Error -> {
                    call.respond(HttpStatusCode.BadRequest, result.errorType.description)
                    callEvent.error(result.errorType)
                }
                WebServerResult.Ok -> {
                    call.respond(HttpStatusCode.OK)
                    callEvent.success()
                }
                else -> callEvent.success()
            }

        } catch (exception: Exception) {
            callEvent.error(exception)
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
        serviceMiddleware.webServerAction(ListenForCommand)
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
            serviceMiddleware.webServerAction(ListenForWake(it))
            return WebServerResult.Accepted(it.toString())
        } ?: run {
            return WebServerResult.Error(WakeOptionInvalid)
        }
    }


    /**
     * /api/play-recording
     * POST to play last recorded voice command
     */
    private fun playRecordingPost(): WebServerResult {
        serviceMiddleware.webServerAction(PlayRecording)
        return WebServerResult.Ok
    }


    /**
     * /api/play-recording
     * GET to download WAV data from last recorded voice command
     */
    private suspend fun playRecordingGet(call: ApplicationCall): WebServerResult? {
        call.respondBytes(
            bytes = serviceMiddleware.webServerRequest(PlayRecordingGet),
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
    private suspend fun playWav(call: ApplicationCall, event: Event): WebServerResult {
        if (call.request.contentType() != audioContentType) {
            event.warning(AudioContentTypeWarning)
        }
        //play even without header
        serviceMiddleware.webServerAction(PlayWav(call.receive()))
        return WebServerResult.Ok
    }

    /**
     * /api/set-volume
     * POST to set volume at one or more sites
     * Body text is volume level (0 = off, 1 = full volume)
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private suspend fun setVolume(call: ApplicationCall): WebServerResult {
        //double and float or double not working but string??
        return call.receive<String>().toFloatOrNull()?.let {
            if (it in 0f..1f) {
                serviceMiddleware.webServerAction(SetVolume(it))
                return WebServerResult.Accepted(it.toString())
            } else {
                return WebServerResult.Error(VolumeValueOutOfRange)
            }
        } ?: run {
            return WebServerResult.Error(VolumeValueInvalid)
        }
    }

    /**
     * /api/start-recording
     * POST to have Rhasspy start recording a voice command
     * actually starts a session
     */
    private fun startRecording(): WebServerResult {
        serviceMiddleware.webServerAction(StartRecording)
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
        serviceMiddleware.webServerAction(StopRecording)
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
        serviceMiddleware.webServerAction(Say(call.receive()))
        return WebServerResult.Ok
    }

    sealed interface WebServerResult {

        object Ok : WebServerResult

        class Accepted(val data: String) : WebServerResult

        class Error(val errorType: WebServerServiceErrorType) : WebServerResult

    }

}