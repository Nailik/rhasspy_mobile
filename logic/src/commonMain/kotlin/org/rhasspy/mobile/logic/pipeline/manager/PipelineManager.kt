package org.rhasspy.mobile.logic.pipeline.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent
import org.rhasspy.mobile.logic.domains.mic.IMicDomain
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.logic.pipeline.PipelineEvent
import org.rhasspy.mobile.logic.pipeline.PipelineState
import org.rhasspy.mobile.logic.pipeline.PipelineState.*
import org.rhasspy.mobile.logic.pipeline.PipelineState.SessionState.*

abstract class PipelineManager(
    private val indication: IIndication,
    private val localAudioPlayer: ILocalAudioPlayer,
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
            DetectState -> {
                indication.onIdle()
                //TODO only if wake word enabled
                micDomain.startRecording()
            }

            is TranscribeState -> {
                indication.onRecording()
                audioFocus.request(AudioFocusRequestReason.Dialog)
                micDomain.startRecording()
            }

            is RecognizeState -> {

            }

            is HandleState -> {

            }

            is TtsState    -> {

            }

            is SpeakState -> {

            }

        }
    }

    private fun endState(state: PipelineState) {
        when (state) {
            DetectState -> {

                micDomain.stopRecording()
            }

            is TranscribeState -> {
                state.timeoutJob.cancel()
                audioFocus.abandon(AudioFocusRequestReason.Dialog)
                micDomain.stopRecording()
            }


            is RecognizeState -> {
                state.timeoutJob.cancel()
            }

            is HandleState -> {
                state.timeoutJob.cancel()
            }

            TtsState       -> {

            }

            SpeakState -> {

            }
        }
    }

}