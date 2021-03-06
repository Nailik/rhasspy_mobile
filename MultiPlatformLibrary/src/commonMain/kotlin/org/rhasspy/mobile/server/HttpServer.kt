package org.rhasspy.mobile.server

import co.touchlab.kermit.Logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.dataconversion.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.nativeutils.installCallLogging
import org.rhasspy.mobile.nativeutils.installCompression
import org.rhasspy.mobile.services.RhasspyActions
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.native.concurrent.ThreadLocal

//https://rhasspy.readthedocs.io/en/latest/reference/#http-api
/**
 * same endpoints as a rhasspy server sends the requests to the according service (mostly state machine)
 */
@ThreadLocal
object HttpServer {
    private val logger = Logger.withTag("HttpServer")

    private var server: CIOApplicationEngine? = null

    fun start() {
        logger.v { "startHttpServer" }

        if (server == null || !ConfigurationSettings.isHttpServerEnabled.value) {
            ConfigurationSettings.httpServerPort.value.toIntOrNull()?.also { port ->
                logger.v { "server == null" }
                server = getServer(port)
                CoroutineScope(Dispatchers.Main).launch {
                    //necessary else netty has problems when the coroutine scope is closed
                    server?.start()
                }
            } ?: run {
                logger.w { "configured server port is not a valid number ${ConfigurationSettings.httpServerPort.data}" }
            }
        } else {
            logger.v {
                if (server != null) {
                    "server already running"
                } else {
                    "webserver disabled"
                }
            }
        }
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

            routing {
                listenForCommand()
                listenForWake()
                playRecordingPost()
                playRecordingGet()
                playWav()
                setVolume()
                startRecording()
                stopRecording()
                say()

                get("/") {
                    call.respondText("working")
                }
            }
        }
    }

    fun stop() {
        logger.v { "stop" }

        server?.stop(0, 0)
        server = null
    }


    /**
     * /api/listen-for-command
     * POST to wake Rhasspy up and start listening for a voice command
     * Returns intent JSON when command is finished
     * ?nohass=true - stop Rhasspy from handling the intent
     * ?timeout=<seconds> - override default command timeout
     * ?entity=<entity>&value=<value> - set custom entities/values in recognized intent
     */
    private fun Routing.listenForCommand() = post("/api/listen-for-command") {
        logger.v { "post /api/listen-for-command" }

        StateMachine.hotWordDetected("REMOTE")
    }


    /**
     * /api/listen-for-wake
     * POST "on" to have Rhasspy listen for a wake word
     * POST "off" to disable wake word
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private fun Routing.listenForWake() = post("/api/listen-for-wake") {
        logger.v { "post /api/listen-for-wake" }

        val action = when (call.receive<String>()) {
            "on" -> true
            "off" -> false
            else -> null
        }

        logger.v { "received $action" }

        action?.also {
            AppSettings.isHotWordEnabled.value = it
        } ?: run {
            logger.w { "invalid body" }
        }

    }


    /**
     * /api/play-recording
     * POST to play last recorded voice command
     */
    private fun Routing.playRecordingPost() = post("/api/play-recording") {
        logger.v { "post /api/play-recording" }

        StateMachine.playRecording()
    }


    /**
     * /api/play-recording
     * GET to download WAV data from last recorded voice command
     */
    private fun Routing.playRecordingGet() = get("/api/play-recording") {
        logger.v { "get /api/play-recording" }

        call.respondBytes(
            bytes = StateMachine.getPreviousRecording().toByteArray(),
            contentType = ContentType("audio", "wav")
        )
    }


    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private fun Routing.playWav() = post("/api/play-wav") {
        logger.v { "post /api/play-wav" }

        if (call.request.contentType() != ContentType("audio", "wav")) {
            logger.w { "invalid content type ${call.request.contentType()}" }
        }
        //play even without header
        StateMachine.playAudio(call.receive())
    }

    /**
     * /api/set-volume
     * POST to set volume at one or more sites
     * Body text is volume level (0 = off, 1 = full volume)
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private fun Routing.setVolume() = post("/api/set-volume") {
        logger.v { "post /api/set-volume" }

        //double and float or double not working but string??
        val volume = call.receive<String>().toFloatOrNull()

        volume?.also {
            if (volume > 0F && volume < 1F) {
                AppSettings.volume.value = volume
            }
            return@post
        }

        logger.w { "invalid volume $volume" }

    }

    /**
     * /api/start-recording
     * POST to have Rhasspy start recording a voice command
     * actually starts a session
     */
    private fun Routing.startRecording() = post("/api/start-recording") {
        logger.v { "post /api/start-recording" }

        StateMachine.startListening()
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
    private fun Routing.stopRecording() = post("/api/stop-recording") {
        logger.v { "post /api/stop-recording" }

        StateMachine.stopListening()
    }

    /**
     * /api/say
     *
     * custom endpoint for the rhasspy app
     * POST text to have Rhasspy use the text-to-speech endpoint to translate the text to audio
     * Afterwards Rhasspy will use the audio endpoint to play the audio
     * just like using say in the ui start screen but remote
     */
    private fun Routing.say() = post("/api/say") {
        logger.v { "post /api/say" }

        RhasspyActions.say(call.receive())
    }

}
