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
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.rhasspy.mobile.logger.Event
import org.rhasspy.mobile.logger.EventLogger
import org.rhasspy.mobile.logger.EventTag
import org.rhasspy.mobile.logger.EventType
import org.rhasspy.mobile.nativeutils.installCallLogging
import org.rhasspy.mobile.nativeutils.installCompression
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.dialogManager.IDialogManagerService
import org.rhasspy.mobile.services.settings.AppSettingsService
import org.rhasspy.mobile.services.statemachine.StateMachineService

class WebServerService : IService() {

    private val logger = Logger.withTag("WebServerService")
    private lateinit var server: CIOApplicationEngine
    private var scope = CoroutineScope(Dispatchers.Default)

    private val params by inject<WebServerServiceParams>()
    private val stateMachineService by inject<StateMachineService>()
    private val dialogManagerService by inject<IDialogManagerService>()
    private val appSettingsService by inject<AppSettingsService>()

    private val eventLogger by inject<EventLogger>(named(EventTag.WebServer.name))

    lateinit var callEvent: Event

    init {
        scope = CoroutineScope(Dispatchers.Default)
        if (params.isHttpServerEnabled) {

            val startEvent = eventLogger.event(EventType.WebServerStart)
            callEvent = eventLogger.event(EventType.WebServerIncomingCall)

            logger.v { "starting server" }
            server = getServer(params.httpServerPort)

            scope.launch {
                startEvent.loading()

                //necessary else netty has problems when the coroutine scope is closed
                try {
                    server.start()
                    startEvent.success()
                    callEvent.loading()
                } catch (e: Exception) {
                    startEvent.error(e)
                    logger.e(e) { "While Starting server" }
                }
            }
        } else {
            logger.v { "Server disabled" }
        }
    }

    override fun onClose() {
        if (::server.isInitialized) {
            server.stop()
        }
        scope.cancel()
    }

    private fun getServer(port: Int): CIOApplicationEngine {
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

            install(StatusPages) {
                HttpStatusCode.allStatusCodes
                    .forEach {
                        status(it) { call, status ->
                            if (status != HttpStatusCode.OK && status != HttpStatusCode.Accepted) {
                                call.respondText(text = status.description, status = status)
                                callEvent.error("${call.request.path()} $status")
                            } else {
                                callEvent.success(call.request.path())
                            }
                            callEvent = eventLogger.event(EventType.WebServerIncomingCall)
                            callEvent.loading()
                        }
                    }
            }

            routing {
                WebServerPath.values().forEach { path ->
                    try {
                        when (path.type) {
                            WebServerPath.WebServerCallType.POST -> post(path.path) {
                                logger.v { "post ${path.path}" }
                                evaluateCall(path, call)
                            }
                            WebServerPath.WebServerCallType.GET -> get(path.path) {
                                logger.v { "get ${path.path}" }
                                evaluateCall(path, call)
                            }
                        }
                    } catch (e: Exception) {
                        logger.e(e) { "While receiving" }
                    }
                }
            }
        }
    }

    private suspend fun evaluateCall(path: WebServerPath, call: ApplicationCall) {
        when (path) {
            WebServerPath.ListenForCommand -> listenForCommand(call)
            WebServerPath.ListenForWake -> listenForWake(call)
            WebServerPath.PlayRecordingPost -> playRecordingPost(call)
            WebServerPath.PlayRecordingGet -> playRecordingGet(call)
            WebServerPath.PlayWav -> playWav(call)
            WebServerPath.SetVolume -> setVolume(call)
            WebServerPath.StartRecording -> startRecording(call)
            WebServerPath.StopRecording -> stopRecording(call)
            WebServerPath.Say -> say(call)
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
    private suspend fun listenForCommand(call: ApplicationCall) {
        call.respond(HttpStatusCode.OK)
        dialogManagerService.listenForCommandWebServer()
    }


    /**
     * /api/listen-for-wake
     * POST "on" to have Rhasspy listen for a wake word
     * POST "off" to disable wake word
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private suspend fun listenForWake(call: ApplicationCall) {
        val action = when (call.receive<String>()) {
            "on" -> true
            "off" -> false
            else -> null
        }

        action?.also {
            call.respond(HttpStatusCode.Accepted)
            appSettingsService.toggleListenForWakeWebServer(it)
        } ?: run {
            call.respond(HttpStatusCode.BadRequest, "Invalid value, allowed: \"on\", \"off\"")
        }
    }


    /**
     * /api/play-recording
     * POST to play last recorded voice command
     */
    private suspend fun playRecordingPost(call: ApplicationCall) {
        call.respond(HttpStatusCode.OK)
        stateMachineService.playRecordingPostWebServer()
    }


    /**
     * /api/play-recording
     * GET to download WAV data from last recorded voice command
     */
    private suspend fun playRecordingGet(call: ApplicationCall) {
        call.respondBytes(
            bytes = stateMachineService.playRecordingGetWebServer().toByteArray(),
            contentType = ContentType("audio", "wav")
        )
    }

    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private suspend fun playWav(call: ApplicationCall) {
        if (call.request.contentType() != ContentType("audio", "wav")) {
            logger.w { "invalid content type ${call.request.contentType()}" }
            callEvent.warning("Missing Content Type")
        }
        call.respond(HttpStatusCode.OK)
        //play even without header
        stateMachineService.playWavWebServer(call.receive<ByteArray>().toList())
    }

    /**
     * /api/set-volume
     * POST to set volume at one or more sites
     * Body text is volume level (0 = off, 1 = full volume)
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private suspend fun setVolume(call: ApplicationCall) {
        //double and float or double not working but string??
        call.receive<String>().toFloatOrNull()?.also {
            if (it in 0f..1f) {
                call.respond(HttpStatusCode.Accepted)
                appSettingsService.setVolumeWebServer(it)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid volume, allowed: 0f-1f")
            }
        } ?: run {
            call.respond(HttpStatusCode.BadRequest, "Missing volume, allowed: 0f-1f")
        }
    }

    /**
     * /api/start-recording
     * POST to have Rhasspy start recording a voice command
     * actually starts a session
     */
    private suspend fun startRecording(call: ApplicationCall) {
        call.respond(HttpStatusCode.OK)
        dialogManagerService.startRecordingWebServer()
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
    private suspend fun stopRecording(call: ApplicationCall) {
        call.respond(HttpStatusCode.OK)
        dialogManagerService.stopRecordingWebServer()
    }

    /**
     * /api/say
     *
     * custom endpoint for the rhasspy app
     * POST text to have Rhasspy use the text-to-speech endpoint to translate the text to audio
     * Afterwards Rhasspy will use the audio endpoint to play the audio
     * just like using say in the ui start screen but remote
     */
    private suspend fun say(call: ApplicationCall) {
        call.respond(HttpStatusCode.OK)
        stateMachineService.sayWebServer(call.receive())
    }

}