package org.rhasspy.mobile.services.http

import co.touchlab.kermit.Logger
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.services.ForegroundService
import org.rhasspy.mobile.services.RecordingService
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.services.http.HttpMethodWrapper.GET
import org.rhasspy.mobile.services.http.HttpMethodWrapper.POST
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.services.native.NativeServer
import org.rhasspy.mobile.settings.AppSettings
import kotlin.native.concurrent.ThreadLocal

//https://rhasspy.readthedocs.io/en/latest/reference/#http-api
@ThreadLocal
object HttpServer {
    private val logger = Logger.withTag(this::class.simpleName!!)

    private var server: NativeServer? = null

    fun start() {
        logger.v { "startHttpServer" }

        if (server == null) {
            logger.v { "server == null" }
            server = NativeServer.getServer(
                listOf(
                    listenForWake(),
                    playRecordingPost(),
                    playRecordingGet(),
                    playWav(),
                    setVolume(),
                    startRecording(),
                    stopRecording()
                )
            )
            server?.start()
        }
    }


    fun stop() {
        logger.v { "stop" }

        server?.stop()
        server = null
    }

    /**
     * /api/listen-for-wake
     * POST "on" to have Rhasspy listen for a wake word
     * POST "off" to disable wake word
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private fun listenForWake() = HttpCallWrapper("/api/listen-for-wake", POST) {
        logger.v { "post /api/listen-for-wake" }

        val action = when (this.receive<String>()) {
            "on" -> true
            "off" -> false
            else -> null
        }

        logger.v { "received $action" }

        action?.also {
            ServiceInterface.setListenForWake(action)
        } ?: kotlin.run {
            logger.w { "invalid body" }
        }

    }


    /**
     * /api/play-recording
     * POST to play last recorded voice command
     */
    private fun playRecordingPost() = HttpCallWrapper("/api/play-recording", POST) {
        logger.v { "post /api/play-recording" }

        AudioPlayer.playData(RecordingService.getLatestRecording())
    }


    /**
     * /api/play-recording
     * GET to download WAV data from last recorded voice command
     */
    private fun playRecordingGet() = HttpCallWrapper("/api/play-recording", GET) {
        logger.v { "get /api/play-recording" }

        respondBytes(
            bytes = RecordingService.getLatestRecording(),
            contentType = ContentType("audio", "wav")
        )
    }


    /**
     * /api/play-wav
     * POST to play WAV data
     * Make sure to set Content-Type to audio/wav
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private fun playWav() = HttpCallWrapper("/api/play-wav", POST) {
        logger.v { "post /api/play-wav" }

        if (requestContentType() == ContentType("audio", "wav")) {
            ServiceInterface.playAudio(receive())
        } else {
            logger.w { "invalid content type ${requestContentType()}" }
        }
    }

    /**
     * /api/set-volume
     * POST to set volume at one or more sites
     * Body text is volume level (0 = off, 1 = full volume)
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private fun setVolume() = HttpCallWrapper("/api/set-volume", POST) {
        logger.v { "post /api/set-volume" }

        //double and float or double not working but string??
        val volume = receive<String>().toFloatOrNull()

        volume?.also {
            if (volume > 0F && volume < 1F) {
                CoroutineScope(Dispatchers.Main).launch {
                    AppSettings.volume.data = volume
                }
            }
            return@HttpCallWrapper
        }

        logger.w { "invalid volume $volume" }

    }

    /**
     * /api/start-recording
     * POST to have Rhasspy start recording a voice command
     */
    private fun startRecording() = HttpCallWrapper("/api/start-recording", POST) {
        logger.v { "post /api/start-recording" }

        CoroutineScope(Dispatchers.Main).launch {
            ServiceInterface.startRecording()
        }
    }

    /**
     * /api/stop-recording
     * POST to have Rhasspy stop recording and process recorded data as a voice command
     * Returns intent JSON when command has been processed
     * ?nohass=true - stop Rhasspy from handling the intent
     * ?entity=<entity>&value=<value> - set custom entity/value in recognized intent
     */
    private fun stopRecording() = HttpCallWrapper("/api/stop-recording", POST) {
        logger.v { "post /api/stop-recording" }

        CoroutineScope(Dispatchers.Main).launch {
            ServiceInterface.stopRecording()
        }
    }

}
