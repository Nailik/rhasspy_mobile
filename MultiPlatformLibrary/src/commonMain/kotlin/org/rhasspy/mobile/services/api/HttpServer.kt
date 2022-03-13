package org.rhasspy.mobile.services.api

import co.touchlab.kermit.Logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.rhasspy.mobile.services.ForegroundService
import org.rhasspy.mobile.services.RecordingService
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.settings.AppSettings
import kotlin.native.concurrent.ThreadLocal

//https://rhasspy.readthedocs.io/en/latest/reference/#http-api
private val logger = Logger.withTag(HttpServer::class.simpleName!!)

@ThreadLocal
object HttpServer {

    private var server: ApplicationEngine? = null

    fun start() {
        logger.v { "start" }

        server = embeddedServer(CIO, 12101) {
            routing {
                listenForWake()
                playRecording()
                playWav()
                setVolume()
                startRecording()
                stopRecording()
            }
        }.start(wait = false)
    }


    fun stop() {
        logger.v { "stop" }

        server?.stop(0, 0)
        server = null
    }

}

/**
 * /api/listen-for-wake
 * POST "on" to have Rhasspy listen for a wake word
 * POST "off" to disable wake word
 * ?siteId=site1,site2,... to apply to specific site(s)
 */
private fun Routing.listenForWake() {
    this.post("/api/listen-for-wake") {
        logger.v { "post /api/listen-for-wake" }

        val action = when (call.receive<String>()) {
            "on" -> true
            "off" -> false
            else -> null
        }

        logger.v { "received $action" }

        action?.also {
            ForegroundService.setListenForWake(action)
        } ?: kotlin.run {
            logger.w { "invalid body" }
        }
    }

}


/**
 * /api/play-recording
 * POST to play last recorded voice command
 * GET to download WAV data from last recorded voice command
 */
private fun Routing.playRecording() {
    this.post("/api/play-recording") {
        logger.v { "post /api/play-recording" }

        AudioPlayer.playRecording(RecordingService.getLatestRecording())
    }

    this.get("/api/play-recording") {
        logger.v { "get /api/play-recording" }

        call.respondBytes(
            bytes = RecordingService.getLatestRecording(),
            contentType = ContentType("audio", "wav")
        )
    }
}


/**
 * /api/play-wav
 * POST to play WAV data
 * Make sure to set Content-Type to audio/wav
 * ?siteId=site1,site2,... to apply to specific site(s)
 */
private fun Routing.playWav() {
    this.post("/api/play-wav") {
        logger.v { "post /api/play-wav" }

        if (call.request.contentType() == ContentType("audio", "wav")) {
            ServiceInterface.playAudio(call.receive())
        } else {
            logger.w { "invalid content type ${call.request.contentType()}" }
        }
    }
}

/**
 * /api/set-volume
 * POST to set volume at one or more sites
 * Body text is volume level (0 = off, 1 = full volume)
 * ?siteId=site1,site2,... to apply to specific site(s)
 */
private fun Routing.setVolume() {
    this.post("/api/set-volume") {
        logger.v { "post /api/set-volume" }

        val volume = call.receive<Double>()

        if (volume > 0 && volume < 1) {
            AppSettings.volume.data = volume
        } else {
            logger.w { "invalid volume $volume" }
        }
    }
}

/**
 * /api/start-recording
 * POST to have Rhasspy start recording a voice command
 */
private fun Routing.startRecording() {
    this.post("/api/start-recording") {
        logger.v { "post /api/start-recording" }

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
private fun Routing.stopRecording() {
    this.post("/api/stop-recording") {
        logger.v { "post /api/stop-recording" }

        ServiceInterface.stopRecording()
    }
}