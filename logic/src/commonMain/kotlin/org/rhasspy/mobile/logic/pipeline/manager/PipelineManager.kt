package org.rhasspy.mobile.logic.pipeline.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.asr.IntentResult
import org.rhasspy.mobile.logic.domains.asr.TranscriptResult
import org.rhasspy.mobile.logic.domains.intent.IIntentDomain
import org.rhasspy.mobile.logic.domains.mic.IMicDomain
import org.rhasspy.mobile.logic.domains.vad.IVadDomain
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.pipeline.PipelineEvent
import org.rhasspy.mobile.logic.pipeline.PipelineResult
import org.rhasspy.mobile.logic.pipeline.PipelineState
import org.rhasspy.mobile.logic.pipeline.PipelineState.*
import org.rhasspy.mobile.logic.pipeline.PipelineState.SessionState.*
import org.rhasspy.mobile.logic.pipeline.WakeResult

//TODO read https://github.com/rhasspy/rhasspy/wiki/Intent-Handling-with-HA-and-Intents
interface Pipeline {

    val stateFlow: StateFlow<sdf>

    suspend fun awaitPipelineResult(): PipelineResult

}

//TODO run pipeline end -> if succes indication idle if not indication error

//TODO run pipelines after wakeword
//TODO when no pipeline is running, say and playbytes endpoints (mqtt and webserver) are allowed
//TODO after pipeline end indication is reset

fun main(pipelineImpl: PipelineImpl,
         private val wakeDomain: IWakeDomain,
         private val micDomain: IMicDomain,) {

    //TODO restarts domains. Domain factory with params -> observers params, stops pipeline, restarts services, starts pipeline

    //wait for waake domain to dtecet wait
    val wake = when (val result = wakeDomain.awaitDetection(micDomain.audioStream)) {
        is WakeResult.Detection   -> result
        is WakeResult.NotDetected -> return result
    }

    //TODO else wait for startListening From MQTT or startRecording form Webserver
    //TODO -> Start pipeline (if not already running)


    when (pipelineImpl.awaitPipelineResult()) {
        IntentResult.NotRecognized       -> TODO()
        TranscriptResult.TranscriptError -> TODO()
    }
}


class PipelineImpl(
    private val micDomain: IMicDomain,
    private val vadDomain: IVadDomain,
    private val asrDomain: IAsrDomain,
    private val intentDomain: IIntentDomain
) : Pipeline {
    override suspend fun awaitPipelineResult(/*onNewState: (state: State) -> Unit*/): PipelineResult {



        val transcript = when (val result = asrDomain.awaitTranscript(micDomain.getAudio(), vadDomain.voice(transcript))) {
            is TranscriptResult.Transcript      -> result
            is TranscriptResult.TranscriptError -> return result
        }

        //state speichern

        val intent = when (val result = intentDomain.awaitIntent(transcript)) {
            is IntentResult.Intent        -> result
            is IntentResult.NotRecognized -> return result
        }


    }

}

abstract class PipelineManager(
    private val indication: IIndication,
    private val audioFocus: IAudioFocus,
    private val micDomain: IMicDomain
) {

    val pipelineHistory: MutableStateFlow<List<PipelineState>> = MutableStateFlow(emptyList())

    abstract fun initialize()

    abstract fun onEvent(event: PipelineEvent)
    abstract fun onEvent(event: MqttConnectionEvent)
    abstract fun onEvent(event: WebServerConnectionEvent)


    protected var currentState: PipelineState = DetectState
        private set

    protected fun goToState(state: PipelineState) {
        endState(currentState)

        currentState = state

        startState(currentState)

        pipelineHistory.update { list ->
            list.toMutableList().apply {
                add(state)
                while (size > 100) {
                    removeLast()
                }
            }
        }
    }

    private fun startState(state: PipelineState) {
        when (state) {
            DetectState        -> {
                indication.onIdle()
                //TODO only if wake word enabled
                micDomain.startRecording()
            }

            is TranscribeState -> {
                indication.onRecording()
                audioFocus.request(AudioFocusRequestReason.Dialog)
                micDomain.startRecording()
            }

            is RecognizeState  -> {

            }

            is HandleState     -> {

            }

            is SpeakState      -> {

            }

            is PlayState       -> {

            }

        }
    }

    private fun endState(state: PipelineState) {
        when (state) {
            DetectState        -> {

                micDomain.stopRecording()
            }

            is TranscribeState -> {
                state.timeoutJob.cancel()
                audioFocus.abandon(AudioFocusRequestReason.Dialog)
                micDomain.stopRecording()
            }


            is RecognizeState  -> {
                state.timeoutJob.cancel()
            }

            is HandleState     -> {
                state.timeoutJob.cancel()
            }

            SpeakState         -> {

            }

            is PlayState       -> {

            }
        }
    }

}