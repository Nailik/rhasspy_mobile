package org.rhasspy.mobile.services.webserver

import co.touchlab.kermit.Logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.RhasspyActions
import org.rhasspy.mobile.services.ServiceError
import org.rhasspy.mobile.services.webserver.data.WebServerLinkStateType
import org.rhasspy.mobile.services.webserver.data.WebServerPath
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

class WebServerService : IService<WebServerLinkStateType>() {

    private val logger = Logger.withTag("WebServerService")
    private val _currentError = MutableSharedFlow<ServiceError<WebServerLinkStateType>?>()
    override val currentError = _currentError.readOnly

    override fun onStart(scope: CoroutineScope): WebServerLink {
        val webServerLink = WebServerLink(
            ConfigurationSettings.isHttpServerEnabled.value,
            ConfigurationSettings.httpServerPort.value,
            ConfigurationSettings.isHttpServerSSLEnabled.value
        )

        scope.launch {
            webServerLink.receivedRequest.collect {
                when (it.path) {
                    WebServerPath.ListenForCommand -> listenForCommand()
                    WebServerPath.ListenForWake -> listenForWake(it.call)
                    WebServerPath.PlayRecordingPost -> playRecordingPost()
                    WebServerPath.PlayRecordingGet -> playRecordingGet(it.call)
                    WebServerPath.PlayWav -> playWav(it.call)
                    WebServerPath.SetVolume -> setVolume(it.call)
                    WebServerPath.StartRecording -> startRecording()
                    WebServerPath.StopRecording -> stopRecording()
                    WebServerPath.Say -> say(it.call)
                }
            }
        }

        return webServerLink
    }


    /**
     * /api/listen-for-command
     * POST to wake Rhasspy up and start listening for a voice command
     * Returns intent JSON when command is finished
     * ?nohass=true - stop Rhasspy from handling the intent
     * ?timeout=<seconds> - override default command timeout
     * ?entity=<entity>&value=<value> - set custom entities/values in recognized intent
     */
    private fun listenForCommand() = StateMachine.hotWordDetected("REMOTE")


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
    private fun playRecordingPost() = StateMachine.playRecording()


    /**
     * /api/play-recording
     * GET to download WAV data from last recorded voice command
     */
    private suspend fun playRecordingGet(call: ApplicationCall) {
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
    private suspend fun playWav(call: ApplicationCall) {
        if (call.request.contentType() != ContentType("audio", "wav")) {
            logger.w { "invalid content type ${call.request.contentType()}" }
        }
        //play even without header
        StateMachine.playAudio(call.receive<ByteArray>().toList())
    }

    /**
     * /api/set-volume
     * POST to set volume at one or more sites
     * Body text is volume level (0 = off, 1 = full volume)
     * ?siteId=site1,site2,... to apply to specific site(s)
     */
    private suspend fun setVolume(call: ApplicationCall) {
        //double and float or double not working but string??
        val volume = call.receive<String>().toFloatOrNull()

        volume?.also {
            if (volume > 0F && volume < 1F) {
                AppSettings.volume.value = volume
            }
            return
        }

        logger.w { "invalid volume $volume" }

    }

    /**
     * /api/start-recording
     * POST to have Rhasspy start recording a voice command
     * actually starts a session
     */
    private fun startRecording() = StateMachine.startListening()

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
    private fun stopRecording() = StateMachine.stopListening()

    /**
     * /api/say
     *
     * custom endpoint for the rhasspy app
     * POST text to have Rhasspy use the text-to-speech endpoint to translate the text to audio
     * Afterwards Rhasspy will use the audio endpoint to play the audio
     * just like using say in the ui start screen but remote
     */
    private suspend fun say(call: ApplicationCall) {
        RhasspyActions.say(call.receive())
    }

}